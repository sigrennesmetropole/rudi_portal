package org.rudi.common.service.helper;

import org.rudi.common.core.security.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Service utilitaire pour récupérer les infos sur l'utilisateur connecté.
 */
@Component
public class UtilContextHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(UtilContextHelper.class);

	/**
	 * Retourne l'utilisateur connecté.
	 *
	 * @return connectedUser
	 */
	public AuthenticatedUser getAuthenticatedUser() {

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		AuthenticatedUser result = null;
		if (auth == null) {
			LOGGER.error("Null authentification");
		} else {
			final Object detail = auth.getDetails();
			if (detail == null) {
				LOGGER.error("User detail is null");
			} else {
				if (detail instanceof AuthenticatedUser) {
					result = (AuthenticatedUser) detail;
				} else {
					LOGGER.error("Unknown authenticated user {}", auth.getPrincipal());
				}
			}

		}
		return result;
	}
}
