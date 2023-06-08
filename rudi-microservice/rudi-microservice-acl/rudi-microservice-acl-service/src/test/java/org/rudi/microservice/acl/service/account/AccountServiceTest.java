package org.rudi.microservice.acl.service.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.acl.core.bean.Account;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.service.AclSpringBootTest;
import org.rudi.microservice.acl.storage.dao.accountregistration.AccountRegistrationDao;
import org.rudi.microservice.acl.storage.dao.user.UserDao;
import org.rudi.microservice.acl.storage.entity.accountregistration.AccountRegistrationEntity;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.rudi.microservice.acl.storage.entity.user.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;

@AclSpringBootTest
class AccountServiceTest {

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AccountRegistrationDao accountRegistrationDao;

	@MockBean
	private JavaMailSenderImpl javaMailSender;

	@AfterEach
	public void cleanData() {
		userDao.deleteAll();
		accountRegistrationDao.deleteAll();
	}

	@Test
	void test_checkAccount_KO_when_missingField() {

		// Aucune information de compte remplie
		Account account = new Account();

		// Les infos sont KO : champ manquant
		assertThrows(MissingFieldException.class, () -> accountService.checkAccountCreation(account));

		// On met un login
		account.setLogin("stuff");

		// Les infos sont toujours KO : champ manquant pour mot de passe
		assertThrows(MissingFieldException.class, () -> accountService.checkAccountCreation(account));

		// On met un mot de passe mais pas de login
		account.setLogin(null);
		account.setPassword("stuff2");

		// Les infos sont toujours KO : champ manquant pour login
		assertThrows(MissingFieldException.class, () -> accountService.checkAccountCreation(account));
	}

	@Test
	void test_checkAccount_KO_when_loginNotMail() {

		// Création info de compte
		Account account = new Account();
		account.setPassword("Pas$word123456789456123");

		// On met un login qui n'est pas un mail
		account.setLogin("stuff");

		// Les infos sont KO : on attend un mail
		assertThrows(LoginNotMailException.class, () -> accountService.checkAccountCreation(account));
	}

	@Test
	void test_checkAccount_KO_when_passwordLengthIncorrect() {

		// Création info de compte
		Account account = new Account();
		account.setLogin("someone@gmail.com");

		// mot de passe trop court
		account.setPassword("p");

		// Les infos sont KO : mot de passe trop court
		assertThrows(PasswordLengthException.class, () -> accountService.checkAccountCreation(account));

		// mot de passe trop long
		account.setPassword("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
				+ "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

		// Les infos sont KO : mot de passe trop long
		assertThrows(PasswordLengthException.class, () -> accountService.checkAccountCreation(account));
	}

	@Test
	void test_checkAccount_KO_when_loginAlreadyExists() {

		// Création d'un user dans la BDD
		UserEntity user = new UserEntity();
		user.setUuid(UUID.randomUUID());
		user.setLogin("alreadyhere@gmail.com");
		user.setPassword("1Passwordd@mn");
		user.setType(UserType.PERSON);
		userDao.save(user);

		// Création info de compte avec le même login
		Account account = new Account();
		account.setLogin("alreadyhere@gmail.com");
		account.setPassword("soMeth1ngudmngayeahw@za");

		// Les infos sont KO : le login existe déjà
		assertThrows(LoginAlreadyExistsException.class, () -> accountService.checkAccountCreation(account));
	}

	@Test
	void test_registerAccount() throws AppServiceException {
		// Login de l'user de test créé
		String login = "someone@gmail.com";

		// Informations pour la création de compte
		Account account = new Account();
		account.setLogin(login);
		account.setPassword("soMeth1ngudmngayeahw@za");
		account.setFirstname("prénom");
		account.setLastname("nomdefamille");

		doCallRealMethod().when(javaMailSender).createMimeMessage();
		doNothing().when(javaMailSender).send((MimeMessage) any());

		accountService.registerAccount(account);
		assertNotNull(accountRegistrationDao.findByLogin(login));
	}

	@Test
	void test_validateAccount() throws AppServiceException {

		User user = null;
		String login = "someone@gmail.com";

		Account account = new Account();
		account.setLogin(login);
		account.setPassword("soMeth1ngudmngayeahw@za");
		account.setFirstname("prénom");
		account.setLastname("nomdefamille");

		doCallRealMethod().when(javaMailSender).createMimeMessage();
		doNothing().when(javaMailSender).send((MimeMessage) any());

		accountService.registerAccount(account);
		AccountRegistrationEntity accountRegistrationEntity = accountRegistrationDao.findByLogin(account.getLogin());

		user = accountService.validateAccount(accountRegistrationEntity.getToken());

		// s'il existe pas => 400
		assertNotNull(user, "La validation du user a échoué");

		assertNull(accountRegistrationDao.findByLogin(account.getLogin()));

	}

	@Test
	void test_cleanUp() {
		AccountRegistrationEntity accountRegistrationEntity = new AccountRegistrationEntity();
		accountRegistrationEntity.setUuid(UUID.randomUUID());
		accountRegistrationEntity.setLogin("test@test.fr");
		accountRegistrationEntity.setPassword("test@test.fr");

		accountRegistrationEntity.setToken(UUID.randomUUID().toString());
		accountRegistrationEntity.setCreationDate(LocalDateTime.now().minusMonths(1));

		accountRegistrationDao.save(accountRegistrationEntity);

		accountService.cleanExpiredAccounts();

		assertNull(accountService.getAccountByToken(accountRegistrationEntity.getToken()));
	}
}
