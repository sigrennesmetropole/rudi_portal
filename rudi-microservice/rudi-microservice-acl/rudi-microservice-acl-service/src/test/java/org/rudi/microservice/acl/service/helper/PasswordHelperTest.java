/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rudi.microservice.acl.service.SpringBootTestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author FNI18300
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { SpringBootTestApplication.class })
public class PasswordHelperTest {

	@Autowired
	private PasswordHelper passwordHelper;

	@Test
	public void bcryptPasswordEncoder() {

		assertNotNull(passwordHelper);

		System.out.println("fnisseron encrypted password = " + passwordHelper.encodePassword("fnisseron@123"));

		System.out.println("rudi encrypted password = " + passwordHelper.encodePassword("rudi@123"));

		System.out.println("fnisseron = " + passwordHelper.base64Encode("fnisseron:fnisseron@123"));

		System.out.println("rudi = " + passwordHelper.base64Encode("rudi:rudi@123"));
	}

}
