package org.rudi.microservice.acl.facade.config.security.jwt;

import org.rudi.microservice.acl.facade.config.security.SecurityConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 
 * @author FNI18300
 *
 */
public class JwtAuthenticationProcessingFilterConfigurer
		extends AbstractHttpConfigurer<JwtAuthenticationProcessingFilterConfigurer, HttpSecurity> {

	private JwtAuthenticationProvider userAuthenticationProvider;

	private AuthenticationSuccessHandler loginSuccessHandler;

	private AuthenticationFailureHandler loginFailureHandler;

	private String loginParameter;

	private String passwordParameter;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		http.addFilterBefore(
				new JwtAuthenticationProcessingFilter(SecurityConstants.AUTHENTICATE_URL, loginParameter,
						passwordParameter, SecurityConstants.CHECK_CREDENTIAL_URL, userAuthenticationProvider,
						loginSuccessHandler, loginFailureHandler, authenticationManager),
				UsernamePasswordAuthenticationFilter.class);
	}

	public JwtAuthenticationProcessingFilterConfigurer userAuthenticationProvider(
			JwtAuthenticationProvider userAuthenticationProvider) {
		this.userAuthenticationProvider = userAuthenticationProvider;
		return this;
	}

	public JwtAuthenticationProcessingFilterConfigurer loginSuccessHandler(
			AuthenticationSuccessHandler loginSuccessHandler) {
		this.loginSuccessHandler = loginSuccessHandler;
		return this;
	}

	public JwtAuthenticationProcessingFilterConfigurer loginFailureHandler(
			AuthenticationFailureHandler loginFailureHandler) {
		this.loginFailureHandler = loginFailureHandler;
		return this;
	}

	public JwtAuthenticationProcessingFilterConfigurer loginParameter(String loginParameter) {
		this.loginParameter = loginParameter;
		return this;
	}

	public JwtAuthenticationProcessingFilterConfigurer passwordParameter(String passwordParameter) {
		this.passwordParameter = passwordParameter;
		return this;
	}

	public static JwtAuthenticationProcessingFilterConfigurer jwtAuthenticationProcessingConfigurer() {
		return new JwtAuthenticationProcessingFilterConfigurer();
	}
}