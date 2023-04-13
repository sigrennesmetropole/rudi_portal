package org.rudi.microservice.acl.service.captcha.config;

import org.rudi.common.service.webclient.AbstractClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
public class CaptchaClientRegistrationRepository extends AbstractClientRegistrationRepository {

	public CaptchaClientRegistrationRepository(CaptchaConfiguration captchaConfiguration) {
		this.clients.put(CaptchaProperties.REGISTRATION_ID, captchaConfiguration.buildClientRegistration());
	}
}
