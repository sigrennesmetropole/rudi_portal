package org.rudi.common.facade.config.filter;

import javax.servlet.http.HttpServletResponse;

import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BearerTokenFilter extends OncePerRequestFilter {

	private static final int INVALID_TOKEN_STATUS = HttpServletResponse.SC_UNAUTHORIZED;
	private final UtilContextHelper utilContextHelper;

	/**
	 * <b>Avant d'utiliser cette méthode, il faut être sûr de l'ordre des filtres configuré dans la classe WebSecurityConfig du microservice</b>
	 *
	 * @return true si un autre BearerTokenFilter a déjà vérifié le token avant ce filtre dans la chaîne des filtres Spring
	 */
	protected boolean tokenHasNotAlreadyBeenChecked(HttpServletResponse response) {
		return response.getStatus() == INVALID_TOKEN_STATUS
				|| SecurityContextHolder.getContext().getAuthentication() == null;
	}

	protected void setTokenIsValid(AuthenticatedUser authenticatedUser, HttpServletResponse response) {
		utilContextHelper.setAuthenticatedUser(authenticatedUser);
		response.setHeader(HttpHeaders.WWW_AUTHENTICATE, null);
	}

	protected void setTokenIsInvalid(HttpServletResponse response) {
		response.setStatus(INVALID_TOKEN_STATUS);
		response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
	}

}
