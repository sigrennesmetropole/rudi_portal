/**
 * RUDI Portail
 */
package org.rudi.microservice.gateway.facade.config;

import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.facade.config.filter.AbstractJwtTokenUtil;
import org.rudi.common.facade.config.filter.JwtTokenData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * Filtre pour les tokens JWT
 *
 * @author FNI18300
 */
public class JwtWebFilter extends AbstractAuthenticationWebFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtWebFilter.class);

	private final AbstractJwtTokenUtil jwtTokenUtil;
	private final ObjectMapper mapper = new ObjectMapper();

	public JwtWebFilter(final String[] excludeUrlPatterns, AbstractJwtTokenUtil jwtTokenUtil) {
		super(excludeUrlPatterns);
		this.jwtTokenUtil = jwtTokenUtil;
	}

	protected Mono<Authentication> handleToken(String requestAuthentTokenHeader) {
		try {
			// Récupération des données du token
			// Attention la superclass du présent filtre supprime le Bearer déjà
			// mais pour le bon fonctionnement du CommonJwtTokenUtil il faut le remettre
			final JwtTokenData authentJtd = jwtTokenUtil
					.validateToken(AbstractJwtTokenUtil.HEADER_TOKEN_JWT_PREFIX + requestAuthentTokenHeader);

			// Si le token est expiré
			if (!authentJtd.isHasError() && authentJtd.isExpired()) {
				// Génération d'un code HTTP 489
				LOGGER.warn("Le token JWT d'authentification à expiré");
				return Mono.empty();
			} else if (!authentJtd.isHasError() && authentJtd.getSubject() != null && authentJtd.getAccount() != null
					&& SecurityContextHolder.getContext().getAuthentication() == null) {

				// Validation du token
				final AuthenticatedUser user = mapper.convertValue(authentJtd.getAccount(), AuthenticatedUser.class);
				// Application des authorités
				final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						user.getLogin(), null, collectAuthorities(user));
				usernamePasswordAuthenticationToken.setDetails(user);

				return Mono.just(usernamePasswordAuthenticationToken);
			} else {
				// On considère que le token est invalide
				LOGGER.warn("Le token reçu par Gateway n'est pas un token JWT valide");
				return Mono.empty();
			}
		} catch (Exception e) {
			LOGGER.warn("JWT Gateway authentication failed", e);
			return Mono.empty();
		}
	}

}
