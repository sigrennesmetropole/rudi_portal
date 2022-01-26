/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
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
import org.springframework.util.Assert;

/**
 * @author FNI18300
 *
 */
public class JwtAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProcessingFilter.class);

	@Value("${security.jwt.parameter.login:login}")
	private String loginParameter;

	@Value("${security.jwt.parameter.password:password}")
	private String passwordParameter;

	private AntPathRequestMatcher checkCredentialMatcher;

	private JwtAuthenticationProvider authenticationProvider;

	/**
	 * Constructor
	 *
	 * @param authenticateUrl
	 * @param checkCredentialUrl
	 * @param authenticationProvider
	 * @param successHandler         - Success Handler
	 * @param failureHandler         - Failure Handler
	 * @param manager
	 */
	public JwtAuthenticationProcessingFilter(String authenticateUrl, String checkCredentialUrl,
			JwtAuthenticationProvider authenticationProvider, final AuthenticationSuccessHandler successHandler,
			final AuthenticationFailureHandler failureHandler, AuthenticationManager manager) {
		super(new AntPathRequestMatcher(authenticateUrl, "POST"));
		setCheckCredential(checkCredentialUrl);
		setAuthentificationProvider(authenticationProvider);
		setAuthenticationSuccessHandler(successHandler);
		setAuthenticationFailureHandler(failureHandler);
		setAuthenticationManager(manager);
	}

	private void setAuthentificationProvider(JwtAuthenticationProvider authenticationProvider) {
		Assert.notNull(authenticationProvider, "authenticationProvider cannot be null");
		this.authenticationProvider = authenticationProvider;
	}

	private void setCheckCredential(String checkCredentialUrl) {
		Assert.notNull(checkCredentialUrl, "checkCredentialUrl cannot be null");
		this.checkCredentialMatcher = new AntPathRequestMatcher(checkCredentialUrl, "POST");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		if (!HttpMethod.POST.name().equals(request.getMethod())) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Authentication method not supported. Request method: {}", request.getMethod());
			}
			throw new AuthenticationCredentialsNotFoundException("Authentication method not supported");
		}

		String login = obtainLogin(request);
		String password = obtainPassword(request);

		if (StringUtils.isNotEmpty(login) && StringUtils.isNotEmpty(password)) {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(login, password, null);

			if (requiresCheckCredentials(request)) {
				LOGGER.debug("login {} founded : try to check credential", token);
				return authenticationProvider.checkCredential(token);
			} else {
				LOGGER.debug("login {} founded : try to authenticate", token);
				return getAuthenticationManager().authenticate(token);
			}
		} else {
			throw new AuthenticationCredentialsNotFoundException("Authentication header not found");
		}
	}

	@Nullable
	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter(passwordParameter);
	}

	@Nullable
	protected String obtainLogin(HttpServletRequest request) {
		return request.getParameter(loginParameter);
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

	@Override
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		boolean result = super.requiresAuthentication(request, response);
		if (!result) {
			result = requiresCheckCredentials(request);
		}
		return result;
	}

	protected boolean requiresCheckCredentials(HttpServletRequest request) {
		return checkCredentialMatcher.matches(request);
	}

}
