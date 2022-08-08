/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.helper;

import org.junit.jupiter.api.Test;
import org.rudi.microservice.acl.service.AclSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author FNI18300
 *
 */
@AclSpringBootTest
class PasswordHelperTest {

	@Autowired
	private PasswordHelper passwordHelper;

	@Test
	void bcryptPasswordEncoder() {

		assertNotNull(passwordHelper);

		System.out.println("fnisseron encrypted password = " + passwordHelper.encodePassword("projekt"));
	}

}
