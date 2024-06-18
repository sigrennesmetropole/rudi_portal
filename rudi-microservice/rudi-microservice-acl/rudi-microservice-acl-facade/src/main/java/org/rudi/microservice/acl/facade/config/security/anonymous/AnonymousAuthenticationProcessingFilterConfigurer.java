package org.rudi.microservice.acl.facade.config.security.anonymous;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 
 * @author FNI18300
 *
 */
public class AnonymousAuthenticationProcessingFilterConfigurer
		extends AbstractHttpConfigurer<AnonymousAuthenticationProcessingFilterConfigurer, HttpSecurity> {

	private AuthenticationSuccessHandler anonymousSuccessHandler;

	private AuthenticationFailureHandler loginFailureHandler;

	private String loginAnonymous;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
		http.addFilterBefore(new AnonymousAuthenticationProcessingFilter(loginAnonymous, anonymousSuccessHandler,
				loginFailureHandler, authenticationManager), AnonymousAuthenticationFilter.class);
	}

	public AnonymousAuthenticationProcessingFilterConfigurer loginSuccessHandler(
			AuthenticationSuccessHandler anonymousSuccessHandler) {
		this.anonymousSuccessHandler = anonymousSuccessHandler;
		return this;
	}

	public AnonymousAuthenticationProcessingFilterConfigurer loginFailureHandler(
			AuthenticationFailureHandler loginFailureHandler) {
		this.loginFailureHandler = loginFailureHandler;
		return this;
	}

	public AnonymousAuthenticationProcessingFilterConfigurer loginAnonymous(String loginAnonymous) {
		this.loginAnonymous = loginAnonymous;
		return this;
	}

	public static AnonymousAuthenticationProcessingFilterConfigurer anonymousAuthenticationProcessingConfigurer() {
		return new AnonymousAuthenticationProcessingFilterConfigurer();
	}
}