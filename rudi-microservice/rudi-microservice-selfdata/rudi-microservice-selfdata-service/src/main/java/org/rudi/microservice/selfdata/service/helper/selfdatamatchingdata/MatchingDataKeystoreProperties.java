package org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "rudi.selfdata.matchingdata.keystore")
@Getter
@Setter
public class MatchingDataKeystoreProperties {
	private String keystoreType = "PKCS12";
	private String keystorePath;
	private String keystorePassword;
	private String keyAlias = "selfdata-matchingdata-key";

	public char[] getKeystorePasswordChars() {
		return getAsChars(keystorePassword);
	}

	protected char[] getAsChars(String text) {
		if (StringUtils.isNotEmpty(text)) {
			return text.toCharArray();
		} else {
			return new char[0];
		}
	}
}
