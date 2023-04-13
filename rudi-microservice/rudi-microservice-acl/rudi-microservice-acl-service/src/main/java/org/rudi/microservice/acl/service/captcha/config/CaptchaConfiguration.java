package org.rudi.microservice.acl.service.captcha.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CaptchaConfiguration {
	private final CaptchaProperties captchaProperties;

	public ClientRegistration buildClientRegistration() {
		return ClientRegistration
				.withRegistrationId(CaptchaProperties.REGISTRATION_ID)
				.tokenUri(captchaProperties.getOauth2TokenUri())
				.clientId(captchaProperties.getClientId())
				.clientSecret(captchaProperties.getClientSecret())
				.clientName(captchaProperties.getClientName())
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope(captchaProperties.getScopes())
				.build();
	}
}
