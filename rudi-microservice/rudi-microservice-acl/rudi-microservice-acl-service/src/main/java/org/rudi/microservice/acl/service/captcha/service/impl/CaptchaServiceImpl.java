package org.rudi.microservice.acl.service.captcha.service.impl;

import java.util.List;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.common.service.util.MonoUtils;
import org.rudi.microservice.acl.core.bean.CaptchaModel;
import org.rudi.microservice.acl.service.captcha.config.CaptchaProperties;
import org.rudi.microservice.acl.service.captcha.service.CaptchaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CaptchaServiceImpl implements CaptchaService {
	private final WebClient captchaWebClient;
	private final CaptchaProperties captchaProperties;
	private final List<AbstractCaptchaProcessor> abstractCaptchaProcessors;

	public CaptchaServiceImpl(@Qualifier("captcha_webclient") WebClient webClient, CaptchaProperties captchaProperties, List<AbstractCaptchaProcessor> abstractCaptchaProcessors) {
		this.captchaWebClient = webClient;
		this.captchaProperties = captchaProperties;
		this.abstractCaptchaProcessors = abstractCaptchaProcessors;
	}

	@Override
	public DocumentContent generateCaptcha(String get, String c, String t, String cs, String d) throws ExternalServiceException {
		for (AbstractCaptchaProcessor abstractCaptchaProcessor : abstractCaptchaProcessors) {
			if (abstractCaptchaProcessor.hasToBeUsed(get)) {
				return abstractCaptchaProcessor.generateCaptcha(get, c, t, cs, d);
			}
		}
		return null;
	}

	@Override
	public Boolean validateCaptcha(CaptchaModel captchaModel) throws ExternalServiceException {
		final Mono<Boolean> captchaToValidate = captchaWebClient.post()
				.uri(uriBuilder -> uriBuilder.path(captchaProperties.getValidateCaptchaEndpoint()).build())
				.body(Mono.just(captchaModel), CaptchaModel.class)
				.attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(CaptchaProperties.REGISTRATION_ID))
				.retrieve()
				.bodyToMono(Boolean.class);
		return MonoUtils.blockOrThrow(captchaToValidate, ExternalServiceException.class);
	}
}
