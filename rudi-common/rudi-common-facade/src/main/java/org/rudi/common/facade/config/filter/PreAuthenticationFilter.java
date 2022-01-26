/**
 *
 */
package org.rudi.common.facade.config.filter;

import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author FNI18300
 *
 */
public class PreAuthenticationFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthenticationFilter.class);

	public static final String SEC_LOGIN = "sec-login";
	public static final String SEC_LASTNAME = "sec-lastname";
	public static final String SEC_FIRSTNAME = "sec-firstname";
	public static final String SEC_EMAIL = "sec-email";
	public static final String SEC_ROLES = "sec-roles";
	public static final String SEC_ORGANIZATION = "sec-organization";
	public static final String SEC_TYPE = "sec-type";

	public PreAuthenticationFilter() {
		super();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			final String login = httpServletRequest.getHeader(SEC_LOGIN);
			if (login != null) {
				SecurityContextHolder.getContext().setAuthentication(createAuthentication(httpServletRequest));

				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Populated SecurityContextHolder with pre-auth token: '{}'",
							SecurityContextHolder.getContext().getAuthentication());
				}
			} else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("SecurityContextHolder not populated with pre-auth token");
				}
			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * Construction du token pre-authentification
	 *
	 * @param httpServletRequest
	 * @return
	 */
	private Authentication createAuthentication(HttpServletRequest httpServletRequest) {
		final String login = httpServletRequest.getHeader(SEC_LOGIN);
		final String type = httpServletRequest.getHeader(SEC_TYPE);
		final String rolesString = httpServletRequest.getHeader(SEC_ROLES);
		Set<String> rolesSet = new LinkedHashSet<>();
		List<String> roles = null;
		if (rolesString != null) {
			roles = Arrays.asList(rolesString.split(";"));
			rolesSet.addAll(roles);
		}
		AuthenticatedUser user = new AuthenticatedUser(login, UserType.valueOf(type));
		assignUserData(user, httpServletRequest, roles);

		return new PreAuthenticationToken(login, user, rolesSet);
	}

	private void assignUserData(AuthenticatedUser user, HttpServletRequest httpServletRequest, List<String> roles) {
		user.setEmail(httpServletRequest.getHeader(SEC_EMAIL));
		user.setFirstname(httpServletRequest.getHeader(SEC_FIRSTNAME));
		user.setLastname(httpServletRequest.getHeader(SEC_LASTNAME));
		user.setOrganization(httpServletRequest.getHeader(SEC_ORGANIZATION));
		user.setRoles(roles);
	}

}
