package org.rudi.microservice.acl.service.captcha.service.impl;

import java.net.URI;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.microservice.acl.service.captcha.config.CaptchaProperties;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public abstract class AbstractCaptchaProcessor {
	protected static final String CAPTCHA_TYPE_IMAGE = "image";
	protected static final String CAPTCHA_TYPE_SOUND = "sound";

	private final WebClient captchaWebClient;
	private final CaptchaProperties captchaProperties;

	protected ClientResponse getClientResponse(String get, String c, String t, String cs, String d) {
		return captchaWebClient.get()
				.uri(uriBuilder -> buildPath(uriBuilder, get, c, t, cs, d))
				.accept(MediaType.APPLICATION_JSON)
				.attributes(ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(CaptchaProperties.REGISTRATION_ID))
				.exchange()
				.block();
	}

	/**
	 * Construction de l'url d'appel de l'API de captcha en fonction des params non nuls
	 *
	 * @param builder builder de l'uri
	 * @param get     type de captcha (html, image...)
	 * @param c       param 1
	 * @param t       param 2 utilisé par le component front uniquement
	 * @param cs      param 3 utilisé par le component front uniquement
	 * @param d       param 4 utilisé par le component front uniquement
	 * @return l'uri construite pour appeler l"API externe
	 */
	private URI buildPath(UriBuilder builder, String get, String c, String t, String cs, String d) {
		builder.path(captchaProperties.getCaptchaEndpoint())
				.queryParam("get", get)
				.queryParam("c", c);
		if (t != null) {
			builder.queryParam("t", t);
		}
		if (cs != null) {
			builder.queryParam("cs", cs);
		}
		if (d != null) {
			builder.queryParam("d", d);
		}
		return builder.build();
	}

	protected abstract boolean hasToBeUsed(String typeCaptcha);

	protected abstract DocumentContent generateCaptcha(String get, String c, String t, String cs, String d) throws ExternalServiceException;
}
