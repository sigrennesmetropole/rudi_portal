package org.rudi.microservice.konsult.facade.config.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Nonnull;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultAnonymousAuthenticationFilter extends OncePerRequestFilter {

	@Value("${apimanager.oauth2.client.anonymous.username:anonymous}")
	private String anonymousUsername;

	private final UtilContextHelper utilContextHelper;

	@Override
	protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
		try {
			final var alreadyAuthenticatedUser = utilContextHelper.getAuthenticatedUser();
			log.info("Utilisateur authentifié : {}", alreadyAuthenticatedUser);
			if (alreadyAuthenticatedUser == null) {
				utilContextHelper.setAuthenticatedUser(createAnonymousAuthenticatedUser());
			}
		} catch (RuntimeException e) {
			log.error("Erreur lors de l'authentification automatique en anonymous (anonymousUsername = {})", anonymousUsername, e);
		}

		filterChain.doFilter(request, response);
	}

	@Nonnull
	private AuthenticatedUser createAnonymousAuthenticatedUser() {
		val user = new AuthenticatedUser();
		user.setLogin(anonymousUsername);
		user.setRoles(Collections.singletonList("ANONYMOUS"));
		return user;
	}

}
