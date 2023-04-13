package org.rudi.microservice.acl.service.captcha.service.impl;

import java.io.ByteArrayInputStream;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.ExternalServiceException;
import org.rudi.common.service.util.MonoUtils;
import org.rudi.microservice.acl.service.captcha.config.CaptchaProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CaptchaOtherProcessor extends AbstractCaptchaProcessor {

	public CaptchaOtherProcessor(@Qualifier("captcha_webclient") WebClient captchaWebClient, CaptchaProperties captchaProperties) {
		super(captchaWebClient, captchaProperties);
	}

	@Override
	protected boolean hasToBeUsed(String typeCaptcha) {
		return !StringUtils.equals(typeCaptcha, CAPTCHA_TYPE_IMAGE);
	}

	@Override
	protected DocumentContent generateCaptcha(String get, String c, String t, String cs, String d) throws ExternalServiceException {
		ClientResponse clientResponse = getClientResponse(get, c, t, cs, d);
		if (clientResponse != null) {
			Mono<String> captchaMono = clientResponse.bodyToMono(String.class);
			String response = MonoUtils.blockOrThrow(captchaMono, ExternalServiceException.class);
			ByteArrayInputStream bais = new ByteArrayInputStream(processResponse(response).getBytes());
			return new DocumentContent("stringFile", "text/json", bais.available(), bais);
		}
		return null;
	}

	/**
	 * @param captchaText texte de reponse de l'API captcha
	 * @return reponse procéssée Faire pointer les urls de la reponse vers notre back
	 */
	private String processResponse(String captchaText) {
		return StringUtils.replace(captchaText, CaptchaProperties.PISTE_ENDPOINT, CaptchaProperties.RUDI_ENDPOINT);
	}
}
