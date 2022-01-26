/**
 * RUDI Portail
 */
package org.rudi.common.facade.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
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
public class JwtRequestFilter extends OncePerRequestFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

	@Autowired
	private CommonJwtTokenUtil<Claims> jwtTokenUtil;

	// Controle des patterns des URL
	private AntPathMatcher pathMatcher;

	// Liste des URL à exclure
	private Collection<String> excludeUrlPatterns;

	private final ObjectMapper mapper = new ObjectMapper();

	public JwtRequestFilter(final String[] excludeUrlPatterns) {
		this.excludeUrlPatterns = Arrays.asList(excludeUrlPatterns);
		pathMatcher = new AntPathMatcher();
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws ServletException, IOException {
		if (response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED
				|| SecurityContextHolder.getContext().getAuthentication() == null) {
			// Request token header
			String requestAuthentTokenHeader = request.getHeader(CommonJwtTokenUtil.HEADER_TOKEN_JWT_AUTHENT_KEY);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Header: {}", requestAuthentTokenHeader);
			}

			if (requestAuthentTokenHeader == null) {
				// on a pas de contexte donc on n'est pas authentifié
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} else {
				handleToken(requestAuthentTokenHeader, response);
			}
		}

		chain.doFilter(request, response);
	}

	private void handleToken(String requestAuthentTokenHeader, final HttpServletResponse response) {
		try {// Récupération des données du token
			final JwtTokenData authentJtd = jwtTokenUtil.validateToken(requestAuthentTokenHeader);

			// Si le token est expiré
			if (!authentJtd.isHasError() && authentJtd.isExpired()) {
				// Génération d'un code HTTP 489
				LOGGER.warn("Le token JWT d'authentification à expiré");
				response.setStatus(498);
			} else if (!authentJtd.isHasError() && authentJtd.getSubject() != null
					&& authentJtd.getAccount() != null
					&& SecurityContextHolder.getContext().getAuthentication() == null) {

				// Validation du token
				final AuthenticatedUser user = mapper.convertValue(authentJtd.getAccount(),
						AuthenticatedUser.class);
				// Application des authorités
				final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						user.getLogin(), null, collectAuthorities(user));
				usernamePasswordAuthenticationToken.setDetails(user);

				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			} else {
				// On considère que le token est invalide
				LOGGER.warn("Le token est invalide");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (Exception e) {
			LOGGER.warn("JWT authentication failed", e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
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
