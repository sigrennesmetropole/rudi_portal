/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.helper;

import org.junit.jupiter.api.Test;
import org.rudi.microservice.acl.service.AclSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author FNI18300
 *
 */
@AclSpringBootTest
class PasswordHelperUT {

	@Autowired
	private PasswordHelper passwordHelper;

	@Test
	void bcryptPasswordEncoder() {

		assertNotNull(passwordHelper);

		String[] passwords = {
				"mon-mot-de-passe-en-clair",
		};

		for (final String password : passwords) {
			System.out.println(password + " => " + passwordHelper.encodePassword(password));
		}
	}

	@Test
	void encryptionTest(){
		String password = "Rudi@123!";
		String password2 = "Rudi@123!";

		String encryptedPassword = passwordHelper.encodePassword(password);
		String encryptedPassword1 = passwordHelper.encodePassword(password);
		String encryptedPassword2 = passwordHelper.encodePassword(password2);

		assertEquals(encryptedPassword, encryptedPassword1, String.format("%s == %s", encryptedPassword, encryptedPassword1));
		assertEquals(encryptedPassword, encryptedPassword2, String.format("%s == %s", encryptedPassword, encryptedPassword2));
	}

}
