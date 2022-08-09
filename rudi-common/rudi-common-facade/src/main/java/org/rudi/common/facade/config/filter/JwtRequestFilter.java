/**
 * RUDI Portail
 */
package org.rudi.common.facade.config.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

/**
 * Filtre pour les tokens JWT
 *
 * @author FNI18300
 */
public class JwtRequestFilter extends BearerTokenFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtRequestFilter.class);

	/**
	 * Le code d'erreur à renvoyer au front quand un token d'authent a expiré
	 */
	private static final int HTTP_CODE_TOKEN_EXPIRED = 498;

	@Autowired
	private AbstractJwtTokenUtil jwtTokenUtil;

	// Controle des patterns des URL
	private AntPathMatcher pathMatcher;

	// Liste des URL à exclure
	private Collection<String> excludeUrlPatterns;

	private final ObjectMapper mapper = new ObjectMapper();

	public JwtRequestFilter(final String[] excludeUrlPatterns, final UtilContextHelper utilContextHelper) {
		super(utilContextHelper);
		this.excludeUrlPatterns = Arrays.asList(excludeUrlPatterns);
		pathMatcher = new AntPathMatcher();
	}

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain) throws ServletException, IOException {

		// Si on est pas autorisé (401) ou bien qu'on a pas de contexte d'authent on va gérer :
		// - Soit l'authentification
		// - Soit le refresh token
		// - Soit interompre la chaîne si quelque chose ne va pas
		boolean hasRefreshed = false;
		if (tokenHasNotAlreadyBeenChecked(response)) {

			// Récupération du token qui certifie si on est authentifié ou pas
			String requestAuthentTokenHeader = request.getHeader(AbstractJwtTokenUtil.HEADER_TOKEN_JWT_AUTHENT_KEY);

			// Récupération du refresh token au cas où le token du dessus expire
			String requestXTokenHeader = request.getHeader(AbstractJwtTokenUtil.HEADER_X_TOKEN_KEY);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Header: {}", requestAuthentTokenHeader);
			}

			// Si tout contexte d'authent a disparu on est pas authentifié
			if (requestAuthentTokenHeader == null && requestXTokenHeader == null) {
				setTokenIsInvalid(response);
			}
			// Si on est plus authentifie 'Authorization' vide mais qu'on a un refresh token, on gère la demande de
			// refresh token
			else if(requestAuthentTokenHeader == null) {
				hasRefreshed = handleRefreshToken(response, requestXTokenHeader);
			}
			// Sinon on gère le token 'Authorization' simplement
			else {
				handleToken(requestAuthentTokenHeader, response);
			}
		}

		// Si un token d'authent a expiré ou qu'on a refresh le token on doit bloquer la chaîne de filtres
		if(response.getStatus() != HTTP_CODE_TOKEN_EXPIRED && !hasRefreshed) {
			chain.doFilter(request, response);
		}
	}

	/**
	 * Gère le header : 'Authorization' de la requête pour vérifier l'auteur de la requête grâce aux tokens
	 * @param requestAuthentTokenHeader le token dans 'Authorization'
	 * @param response la réponse qu'on va renvoyer à l'auteur
	 */
	private void handleToken(String requestAuthentTokenHeader, final HttpServletResponse response) {
		try {
			// Récupération des données du token tout en le validant
			final JwtTokenData authentJtd = jwtTokenUtil.validateToken(requestAuthentTokenHeader);

			// Si le token est expiré on veut arrêter la chaîne car l'auteur de la requête n'est plus authentifié
			if (!authentJtd.isHasError() && authentJtd.isExpired()) {
				LOGGER.warn("Le token JWT d'authentification à expiré");
				response.setStatus(HTTP_CODE_TOKEN_EXPIRED);

			} else if (!authentJtd.isHasError() && authentJtd.getSubject() != null && authentJtd.getAccount() != null
					&& SecurityContextHolder.getContext().getAuthentication() == null) {

				// Validation du token
				final var authenticatedUser = mapper.convertValue(authentJtd.getAccount(), AuthenticatedUser.class);
				setTokenIsValid(authenticatedUser, response);
			}
			// Pour tout autre cas on considère que le token est invalide donc que l'auteur est unauthorized
			else {
				LOGGER.warn("Le token n'est pas un token JWT valide. Veuillez consulter les logs DEBUG précédents de AbstractJwtTokenUtil pour plus d'informations sur la cause de l'erreur.");
				setTokenIsInvalid(response);
			}
		}
		// Problème lors du parsing du token JWT : on renvoie 401 unauthorized car celà arrive si le token est malformé
		// donc trafiqué par l'auteur de la requête
		catch (Exception e) {
			LOGGER.warn("JWT authentication failed", e);
			setTokenIsInvalid(response);
		}
	}

	/**
	 * On rafraîchit le token d'authent de l'auteur de la requête
	 * @param response la réponse renvoyée à l'auteur
	 * @param requestXTokenHeader le refresh token
	 * @return si ona réussi à refresh le token ou pas
	 */
	private boolean handleRefreshToken(final HttpServletResponse response, String requestXTokenHeader) {
		try {
			// On essaye de refresh les token à l'aide du refresh token
			Tokens tokens = jwtTokenUtil.generateNewJwtTokens(requestXTokenHeader);
			response.addHeader(AbstractJwtTokenUtil.HEADER_TOKEN_JWT_AUTHENT_KEY, tokens.getJwtToken());
			response.addHeader(AbstractJwtTokenUtil.HEADER_X_TOKEN_KEY, tokens.getRefreshToken());
			response.setStatus(HttpServletResponse.SC_OK);
			return true;
		} catch (RefreshTokenExpiredException|JsonProcessingException|JOSEException e) {
			// En cas d'erreur c'est que même le refresh token a expiré
			LOGGER.warn("Failed to refresh", e);
			setTokenIsInvalid(response);
			return false;
		}
	}

	@Override
	protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
		// Contrôle si l'URL n'est pas dans le liste d'exclusion. Si c'est le cas, elle
		// ne passera pas dans ce filtre
		return excludeUrlPatterns.stream().anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
	}

}
