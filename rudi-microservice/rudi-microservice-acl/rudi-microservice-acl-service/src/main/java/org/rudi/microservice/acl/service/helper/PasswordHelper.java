/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.helper;

import org.apache.commons.lang3.StringUtils;
import org.rudi.microservice.acl.service.account.MissingPasswordChangeFieldException;
import org.rudi.microservice.acl.service.account.PasswordLengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * @author FNI18300
 *
 */
@Component
public class PasswordHelper {

	@Value("${account.min.length.password}")
	private int minLengthPassword;

	@Value("${account.max.length.password}")
	private int maxLengthPassword;

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

	public void checkPassword(String password) throws MissingPasswordChangeFieldException, PasswordLengthException {
		if (password == null) {
			throw new MissingPasswordChangeFieldException("password");
		}
		// Contrôle de la longueur du champ mot de passe
		if (!isPasswordLengthValid(password)) {
			throw new PasswordLengthException(this.minLengthPassword, this.maxLengthPassword);
		}
	}

	/**
	 * Contrôle du champ mot de passe
	 *
	 * @param password le mot de passe
	 * @return si il est valide ou pas
	 */
	private boolean isPasswordLengthValid(String password) {
		// Non nul
		if (StringUtils.isEmpty(password)) {
			return false;
		}

		// Contrôle de longueur
		return (password.length() >= this.minLengthPassword && password.length() <= this.maxLengthPassword);
	}

}
