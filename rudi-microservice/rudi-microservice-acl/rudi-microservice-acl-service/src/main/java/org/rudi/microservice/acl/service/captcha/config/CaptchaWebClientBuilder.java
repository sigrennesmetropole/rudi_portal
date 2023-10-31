package org.rudi.microservice.acl.service.captcha.config;

import javax.net.ssl.SSLException;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.rudi.common.service.webclient.AbstractOauthWebClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CaptchaWebClientBuilder extends AbstractOauthWebClientBuilder {

	private final CaptchaProperties captchaProperties;

	public CaptchaWebClientBuilder(CaptchaClientRegistrationRepository clientRegistrationRepository,
			HttpClientHelper httpClientHelper, CaptchaProperties captchaProperties) {
		super(clientRegistrationRepository, httpClientHelper);
		this.captchaProperties = captchaProperties;
	}

	@Bean(name = "captcha_oauth_webclient_builder")
	public WebClient.Builder captchaOauthWebClientBuilder(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder)
			throws SSLException {
		return oauthWebClientBuilder(jackson2ObjectMapperBuilder);
	}

	@Bean(name = "captcha_webclient")
	public WebClient webClient(@Qualifier("captcha_oauth_webclient_builder") WebClient.Builder oauthWebClientBuilder) {
		return oauthWebClientBuilder.baseUrl(captchaProperties.getCaptchaBaseUrl()).build();
	}

	@Override
	protected boolean isTrustAllCerts() {
		return captchaProperties.isTrustAllCerts();
	}
}
