/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.anonymous;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author FNI18300
 *
 */
public class AnonymousAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnonymousAuthenticationProcessingFilter.class);

	@Value("${security.anonymous.login:anonymous}")
	private String loginAnonymous;

	/**
	 *
	 * Constructor
	 *
	 * @param successHandler - Success Handler
	 * @param failureHandler - Failure Handler
	 */
	public AnonymousAuthenticationProcessingFilter(final AuthenticationSuccessHandler successHandler,
			final AuthenticationFailureHandler failureHandler, AuthenticationManager manager) {
		super(new AntPathRequestMatcher("/anonymous", "POST"));
		this.setAuthenticationSuccessHandler(successHandler);
		this.setAuthenticationFailureHandler(failureHandler);
		this.setAuthenticationManager(manager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (!HttpMethod.POST.name().equals(request.getMethod())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Authentication method not supported. Request method: {}", request.getMethod());
			}
			throw new AuthenticationCredentialsNotFoundException("Authentication method not supported");
		}

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginAnonymous,
				loginAnonymous, null);
		LOGGER.debug("login {} founded : try to authenticate", token);
		return this.getAuthenticationManager().authenticate(token);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		SecurityContextHolder.clearContext();
		this.getFailureHandler().onAuthenticationFailure(request, response, failed);
	}

}
