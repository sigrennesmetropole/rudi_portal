/**
 * 
 */
package org.rudi.facet.generator.pdf.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author FNI18300
 *
 */
@Configuration
@ConfigurationProperties(prefix = "rudi.pdf.sign")
@Getter
@Setter
public class PDFSignerConfiguration {

	private String keyStorePath;

	private String keyStorePassword;

	private String keyStoreKeyAlias;

	private String keyStoreKeyPassword;

	private String keyStoreType = "PKCS12";

	private boolean debug = false;

	public char[] getKeyStorePasswordChars() {
		return getAsChars(keyStorePassword);
	}

	public char[] getKeyStoreKeyPasswordChars() {
		return getAsChars(keyStoreKeyPassword);
	}

	protected char[] getAsChars(String text) {
		if (StringUtils.isNotEmpty(text)) {
			return text.toCharArray();
		} else {
			return new char[0];
		}
	}

}
