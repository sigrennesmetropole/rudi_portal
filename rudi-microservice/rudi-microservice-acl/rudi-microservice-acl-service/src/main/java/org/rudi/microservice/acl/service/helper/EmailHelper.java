/**
 * 
 */
package org.rudi.microservice.acl.service.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.email.exception.EMailException;
import org.rudi.facet.email.model.EMailDescription;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.exception.GenerationModelNotFoundException;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.rudi.microservice.acl.core.bean.User;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.rudi.microservice.acl.service.mapper.UserMapper;
import org.rudi.microservice.acl.storage.dao.user.UserCustomDao;
import org.rudi.microservice.acl.storage.dao.user.UserDao;
import org.rudi.microservice.acl.storage.entity.accountregistration.AccountRegistrationEntity;
import org.rudi.microservice.acl.storage.entity.accountupdate.ResetPasswordRequestEntity;
import org.rudi.microservice.acl.storage.entity.address.AbstractAddressEntity;
import org.rudi.microservice.acl.storage.entity.address.AddressType;
import org.rudi.microservice.acl.storage.entity.address.EmailAddressEntity;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

/**
 * @author fni18300
 *
 */
@Component
@Slf4j
public class EmailHelper {

	private static final String EMAIL_PATTERN = "^(.+)@(\\S+)$";

	@Value("${email.accountcreation.activation.subject}")
	private String accountCreationActivationSubject;

	@Value("${email.accountcreation.activation.body}")
	private String accountCreationActivationBody;

	@Value("${email.accountcreation.registration.subject}")
	private String accountCreationRegistrationSubject;

	@Value("${email.accountcreation.registration.body}")
	private String accountCreationRegistrationBody;

	@Value("${email.urlServer:http://www.rudi.bzh}")
	private String urlServer;

	@Value("${email.accountvalidation.path:/login/accountValidation}")
	private String accountValidationPath;

	@Autowired
	private TemplateGeneratorImpl templateGenerator;

	@Autowired
	private EMailService eMailService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserCustomDao userCustomDao;

	@Autowired
	private UserMapper userMapper;

	@Value("${email.resetpassword.subject}")
	private String changePasswordSubject;

	@Value("${email.resetpassword.body}")
	private String changePasswordBody;

	@Value("${email.resetPasswordPath:/login/reset-password}")
	private String resetPasswordPath;

	@Value("${email.confirmationresetpassword.subject}")
	private String confirmationChangePasswordSubject;

	@Value("${email.confirmationresetpassword.body}")
	private String confirmationChangePasswordBody;

	/**
	 * Envoie un courriel de confirmation de création de compte
	 * 
	 * @param user   l'utilisateur destinataire
	 * @param locale la langue
	 */
	public void sendAccountCreationConfirmation(UserEntity user, Locale locale) {
		try {

			// recherche de l'email de destination
			String to = lookupEmail(user);
			if (to == null) {
				log.info("No email address for {}", user.getLogin());
				return;
			}

			// génération du sujet
			AccountCreationConfirmationDataModel dataModelSubject = new AccountCreationConfirmationDataModel(user,
					urlServer, locale, accountCreationActivationSubject);
			DocumentContent subject = templateGenerator.generateDocument(dataModelSubject);

			// génération du corps
			AccountCreationConfirmationDataModel dataModelBody = new AccountCreationConfirmationDataModel(user, urlServer,
					locale, accountCreationActivationBody);
			DocumentContent body = templateGenerator.generateDocument(dataModelBody);

			// Création du modèle de courrier
			EMailDescription eMailDescription = new EMailDescription(to,
					FileUtils.readFileToString(subject.getFile(), StandardCharsets.UTF_8), body);
			// envoie du courriel
			eMailService.sendMailAndCatchException(eMailDescription);

		} catch (GenerationModelNotFoundException | GenerationException | IOException e) {
			// On ne renvoie pas d'exception car on ne veut pas bloquer la création de compte (mail informatif seulement)
			log.error("Cannot send account creation confirmation mail", e);
		}
	}

	/**
	 * Méthode d'envoie du courriel de création de compte avec demande de confirmation d'adresse email
	 *
	 * @param account le compte
	 * @param locale
	 * @throws GenerationModelNotFoundException
	 * @throws GenerationException
	 * @throws IOException
	 * @throws EMailException
	 */
	public void sendAccountRegistration(AccountRegistrationEntity account, Locale locale)
			throws GenerationModelNotFoundException, GenerationException, IOException, EMailException {
		// recherche de l'email de destination
		String to = lookupEmail(account);
		if (to == null) {
			log.info("No email address for {}", account.getLogin());
			return;
		}

		// génération du sujet
		AccountRegistrationDataModel dataModelSubject = new AccountRegistrationDataModel(account, urlServer, accountValidationPath,
				locale, accountCreationRegistrationSubject);
		DocumentContent subject = templateGenerator.generateDocument(dataModelSubject);

		// génération du corps
		AccountRegistrationDataModel dataModelBody = new AccountRegistrationDataModel(account, urlServer, accountValidationPath,
				locale, accountCreationRegistrationBody);
		DocumentContent body = templateGenerator.generateDocument(dataModelBody);

		// Création du modèle de courrier
			EMailDescription eMailDescription = new EMailDescription(to,
					FileUtils.readFileToString(subject.getFile(), StandardCharsets.UTF_8), body);
			// envoie du courriel
			eMailService.sendMailAndCatchException(eMailDescription);
	}

	private String lookupEmail(AccountRegistrationEntity account) {
		return account.getLogin();
	}

	/**
	 * Extraction de l'adresse courriel cible
	 * 
	 * @param user
	 * @return
	 */
	protected String lookupEmail(UserEntity user) {
		if (user.getLogin().matches(EMAIL_PATTERN)) {
			return user.getLogin();
		} else {
			Optional<AbstractAddressEntity> emailAddressEntity = user.getAddresses().stream()
					.filter(a -> a.getType() == AddressType.EMAIL).findFirst();
			if (emailAddressEntity.isPresent()) {
				return ((EmailAddressEntity) emailAddressEntity.get()).getEmail();
			} else {
				return null;
			}
		}
	}

	public User lookupUserByEmail(String email) {
		UserEntity currentUser = userDao.findByLogin(email);
		//On part du principe que l'email est le login
		if(currentUser == null) {
			UserSearchCriteria criteria = new UserSearchCriteria();
			criteria.setUserEmail(email);
			Pageable pageable = PageRequest.of(0, 1);
			Page<UserEntity> userPage = userCustomDao.searchUsers(criteria, pageable);
			if(userPage != null) {
				currentUser = userPage.stream().findFirst().orElse(null);
			}
		}
		if(currentUser != null) {
			return userMapper.entityToDto(currentUser);
		}
		return null;
	}

	public void sendTokenToChangePassword(ResetPasswordRequestEntity passwordObject, String email, Locale locale) {
		try {
			//Géneration du sujet
			ResetPasswordRequestDataModel dataModelSubject = new ResetPasswordRequestDataModel(passwordObject, urlServer, resetPasswordPath, locale, changePasswordSubject);
			DocumentContent subject = templateGenerator.generateDocument(dataModelSubject);
			log.info("No email address for {}", email);
			// génération du corps
			ResetPasswordRequestDataModel dataModelBody = new ResetPasswordRequestDataModel(passwordObject, urlServer, resetPasswordPath, locale,changePasswordBody);
			DocumentContent body = templateGenerator.generateDocument(dataModelBody);

			// Création du modèle de courrier
			EMailDescription eMailDescription = new EMailDescription(email,
					FileUtils.readFileToString(subject.getFile(), StandardCharsets.UTF_8), body);
			// envoie du courriel
			eMailService.sendMailAndCatchException(eMailDescription);

		} catch (GenerationModelNotFoundException | GenerationException | IOException e) {
			log.error("Cannot send generate token mail", e);
		}
	}

	public void sendPasswordChangeConfirmation(UserEntity user, Locale locale) {
		try {

			// recherche de l'email de destination
			String to = lookupEmail(user);
			if (to == null) {
				log.error("No email address for {}", user.getLogin());
				return;
			}

			// génération du sujet
			final var dataModelSubject = new AccountCreationConfirmationDataModel(user,
					urlServer, locale, confirmationChangePasswordSubject);
			DocumentContent subject = templateGenerator.generateDocument(dataModelSubject);

			// génération du corps
			final var dataModelBody = new AccountCreationConfirmationDataModel(user, urlServer,
					locale, confirmationChangePasswordBody);
			DocumentContent body = templateGenerator.generateDocument(dataModelBody);

			// Création du modèle de courrier
			final var eMailDescription = new EMailDescription(to,
					FileUtils.readFileToString(subject.getFile(), StandardCharsets.UTF_8), body);
			// envoie du courriel
			eMailService.sendMailAndCatchException(eMailDescription);

		} catch (Exception e) {
			// On ne renvoie pas d'exception car on ne veut pas bloquer la création de compte (mail informatif seulement)
			log.error("Cannot send password change confirmation mail", e);
		}
	}
}
