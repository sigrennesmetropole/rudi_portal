/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.helper;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class PasswordHelper {

	@Autowired
	@Qualifier("userPasswordEncoder")
	private PasswordEncoder userPasswordEncoder;

	@Bean("userPasswordEncoder")
	public PasswordEncoder userPasswordEncoder() {
		return new BCryptPasswordEncoder(4);
	}

	public String encodePassword(String password) {
		return userPasswordEncoder.encode(password);
	}

	public String base64Encode(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes());
	}

}
