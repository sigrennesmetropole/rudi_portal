/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.helper;

import org.junit.jupiter.api.Test;
import org.rudi.microservice.acl.service.AclSpringBootTest;
import org.rudi.microservice.acl.storage.entity.address.EmailAddressEntity;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;

/**
 * @author FNI18300
 *
 */
@AclSpringBootTest
class EmailHelperTest {

	@Autowired
	private EmailHelper emailHelper;

	@MockBean
	private JavaMailSenderImpl javaMailSender;

	@Test
	void accountCreationConfirmation() {

		assertNotNull(emailHelper);

		UserEntity user = new UserEntity();
		user.setLogin("user1");
		user.setAddresses(new HashSet<>());
		EmailAddressEntity emailAddress = new EmailAddressEntity();
		emailAddress.setEmail("user1@gmail.com");
		user.getAddresses().add(emailAddress);

		doCallRealMethod().when(javaMailSender).createMimeMessage();
		doNothing().when(javaMailSender).send((MimeMessage) any());

		emailHelper.sendAccountCreationConfirmation(user, Locale.FRENCH);

		UserEntity user2 = new UserEntity();
		user2.setLogin("user2@laposte.net");

		doCallRealMethod().when(javaMailSender).createMimeMessage();
		doNothing().when(javaMailSender).send((MimeMessage) any());

		emailHelper.sendAccountCreationConfirmation(user2, Locale.FRENCH);
	}

}
