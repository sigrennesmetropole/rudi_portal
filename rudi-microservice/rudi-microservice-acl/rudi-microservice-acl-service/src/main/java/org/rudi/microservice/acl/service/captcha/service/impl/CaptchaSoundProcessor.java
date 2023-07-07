package org.rudi.microservice.acl.service.captcha.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.microservice.acl.service.captcha.config.CaptchaProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CaptchaSoundProcessor extends AbstractCaptchaMultimediaProcessor {

	public CaptchaSoundProcessor(@Qualifier("captcha_webclient")WebClient captchaWebClient, CaptchaProperties captchaProperties, ResourceHelper resourceHelper) {
		super(captchaWebClient, captchaProperties, resourceHelper);
	}

	@Override
	protected boolean hasToBeUsed(String typeCaptcha) {
		return StringUtils.equals(typeCaptcha, CAPTCHA_TYPE_SOUND);
	}
}
