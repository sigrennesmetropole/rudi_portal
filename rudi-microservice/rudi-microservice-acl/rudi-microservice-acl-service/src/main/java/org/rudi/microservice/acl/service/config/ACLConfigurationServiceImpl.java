package org.rudi.microservice.acl.service.config;

import org.rudi.common.service.configuration.impl.ConfigurationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
public class ACLConfigurationServiceImpl extends ConfigurationServiceImpl {
	@Value("${rudi.captcha.enabled:true}")
	@Getter
	private boolean captchaEnabled;
}
