/**
 * RUDI Portail
 */
package org.rudi.common.facade.config.filter;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Filtre pour les tokens JWT
 *
 * @author FNI18300
 */
public class OAuth2RequestFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2RequestFilter.class);

	private RestTemplate restTemplate = new RestTemplate();

	// Controle des patterns des URL
	private AntPathMatcher pathMatcher;

	// Liste des URL à exclure
	private Collection<String> excludeUrlPatterns;

	private String checkTokenUri;

	public OAuth2RequestFilter(final String[] excludeUrlPatterns, String checkTokenUri) {
		this.excludeUrlPatterns = Arrays.asList(excludeUrlPatterns);
		pathMatcher = new AntPathMatcher();
		this.checkTokenUri = checkTokenUri;
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws ServletException, IOException {
		// Request token header
		String requestAuthentTokenHeader = request.getHeader(CommonJwtTokenUtil.HEADER_TOKEN_JWT_AUTHENT_KEY);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Header: {}", requestAuthentTokenHeader);
		}
		if (requestAuthentTokenHeader == null) {
			// on a pas de contexte donc on n'est pas authentifié
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else if (!requestAuthentTokenHeader.startsWith(CommonJwtTokenUtil.HEADER_TOKEN_JWT_PREFIX)) {
			LOGGER.error("Le token ne commence pas avec la chaine Bearer");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			final String tokenJwt = requestAuthentTokenHeader.substring(CommonJwtTokenUtil.HEADER_TOKEN_JWT_PREFIX.length());

			handleToken(response, tokenJwt);
		}

		chain.doFilter(request, response);
	}

	private void handleToken(final HttpServletResponse response, final String tokenJwt) {
		try {
			ResponseEntity<OAuth2TokenData> checkToken = restTemplate.getForEntity(checkTokenUri + "?token=" + tokenJwt,
					OAuth2TokenData.class);
			if (checkToken.getStatusCode() == HttpStatus.OK) {
				handleToken(checkToken, response);
			} else {
				// On considère que le token est invalide
				LOGGER.warn("Le token est invalide {}", checkToken.getStatusCode());
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (HttpClientErrorException.BadRequest e) {
			LOGGER.warn("OAuth2 token check failed. See ACL logs for details.", e);
			// c'est la cas d'un token jwt reçu
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			LOGGER.warn("OAuth2 authentication failed", e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private void handleToken(ResponseEntity<OAuth2TokenData> checkToken, final HttpServletResponse response) {
		OAuth2TokenData tokenData = checkToken.getBody();
		if (tokenData != null && tokenData.isActive()) {
			// Validation du token
			AuthenticatedUser user = createAuthenticatedUser(tokenData);
			// Application des authorités
			final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
					user.getLogin(), null, collectAuthorities(user));
			usernamePasswordAuthenticationToken.setDetails(user);

			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		} else {
			// On considère que le token est invalide
			LOGGER.warn("Le token pour {} est inactif", tokenData);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	private AuthenticatedUser createAuthenticatedUser(OAuth2TokenData tokenData) {
		AuthenticatedUser user = new AuthenticatedUser(tokenData.getClientId(), UserType.ROBOT);
		user.setRoles(new ArrayList<>());
		if (CollectionUtils.isNotEmpty(tokenData.getAuthorities())) {
			for (String role : tokenData.getAuthorities()) {
				user.getRoles().add(role);
			}
		}
		return user;
	}

	private Collection<? extends GrantedAuthority> collectAuthorities(AuthenticatedUser user) {
		List<SimpleGrantedAuthority> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(user.getRoles())) {
			for (String role : user.getRoles()) {
				result.add(new SimpleGrantedAuthority(role));
			}
		}
		return result;
	}

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		// Contrôle si l'URL n'est pas dans le liste d'exclusion. Si c'est le cas, elle
		// ne passera pas dans ce filtre
		return excludeUrlPatterns.stream().anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
	}

}
