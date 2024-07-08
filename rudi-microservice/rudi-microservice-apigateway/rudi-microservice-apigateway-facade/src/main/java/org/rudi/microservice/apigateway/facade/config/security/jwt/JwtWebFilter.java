/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.config.security.jwt;

import java.util.List;

import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.rudi.common.facade.config.filter.AbstractJwtTokenUtil;
import org.rudi.common.facade.config.filter.JwtTokenData;
import org.rudi.microservice.apigateway.facade.config.security.AbstractAuthenticationWebFilter;
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

	private static final List<UserType> ACCEPTED_JWT_USER_TYPE = List.of(UserType.PERSON);

	private final AbstractJwtTokenUtil jwtTokenUtil;
	private final ObjectMapper mapper = new ObjectMapper();

	public JwtWebFilter(final String[] excludeUrlPatterns, AbstractJwtTokenUtil jwtTokenUtil) {
		super(excludeUrlPatterns);
		this.jwtTokenUtil = jwtTokenUtil;
	}

	protected Mono<Authentication> handleToken(String requestAuthentTokenHeader) {
		// Récupération des données du token
		// Attention la superclass du présent filtre supprime le Bearer déjà
		// mais pour le bon fonctionnement du CommonJwtTokenUtil il faut le remettre
		final JwtTokenData authentJtd = jwtTokenUtil
				.validateToken(AbstractJwtTokenUtil.HEADER_TOKEN_JWT_PREFIX + requestAuthentTokenHeader);

		if (!authentJtd.isHasError() && authentJtd.getSubject() != null && authentJtd.getAccount() != null
				&& SecurityContextHolder.getContext().getAuthentication() == null) {

			// Validation du token
			final AuthenticatedUser user = mapper.convertValue(authentJtd.getAccount(), AuthenticatedUser.class);
			if (!ACCEPTED_JWT_USER_TYPE.contains(user.getType())) {
				// On considère que le token est invalide pour bloquer les utilisateurs Microservice et API et ROBOT
				LOGGER.warn("Le token reçu par Gateway ne correspond pas à un type autorisé {}", user.getType());
				return Mono.empty();
			} else {
				// Application des authorités
				final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						user.getLogin(), null, collectAuthorities(user));
				usernamePasswordAuthenticationToken.setDetails(user);

				return Mono.just(usernamePasswordAuthenticationToken);
			}
		} else {
			// On considère que le token est invalide
			LOGGER.warn("Le token reçu par Gateway n'est pas un token JWT valide");
			return Mono.empty();
		}
	}

}
