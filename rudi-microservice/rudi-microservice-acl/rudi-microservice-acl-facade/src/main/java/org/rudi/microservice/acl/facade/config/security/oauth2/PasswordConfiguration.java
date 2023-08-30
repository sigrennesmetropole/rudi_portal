/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.facade.config.security.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class PasswordConfiguration {

	@Bean("clientPasswordEncoder")
	public PasswordEncoder clientPasswordEncoder() {
		return new BCryptPasswordEncoder(8);
	}
}
