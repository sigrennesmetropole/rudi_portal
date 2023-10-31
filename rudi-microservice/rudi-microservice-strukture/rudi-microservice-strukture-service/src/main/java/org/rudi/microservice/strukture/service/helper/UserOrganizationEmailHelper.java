package org.rudi.microservice.strukture.service.helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.email.model.EMailDescription;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.exception.GenerationModelNotFoundException;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserOrganizationEmailHelper {

	private static final String EMAIL_PATTERN = "^(.+)@(\\S+)$";

	@Value("${email.organization.password.update.subject}")
	private String organizationUserPasswordChangeSubject;

	@Value("${email.organization.password.update.body}")
	private String organizationUserPasswordChangeBody;

	@Value("${email.urlServer:http://www.rudi.bzh}")
	private String urlServer;

	@Autowired
	private EMailService emailService;

	@Autowired
	private TemplateGeneratorImpl templateGenerator;

	public void sendUserOrganizationUpdatePasswordConfirmation(OrganizationEntity organizaton, List<User> users,
			Locale locale) {

		if (organizaton == null || locale == null) {
			throw new IllegalArgumentException("Argument(s) obligatoire(s) manquant(s) : organization et locale");
		}

		if (CollectionUtils.isEmpty(users)) {
			log.info("Aucun utilisateur d'organisation trouvé pour envoyer des mails");
			return;
		}

		// Construction de la liste des destinataires
		List<String> receivers = users.stream().map(this::getAndCheckEmail).filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());

		// Aucun destinataire ? On fait rien
		if (receivers.isEmpty()) {
			log.error("Aucune adresse déduite pour la liste fournie");
			return;
		}

		try {
			// Génération du sujet
			UserOrganizationPasswordUpdateDataModel dataModelSubject = new UserOrganizationPasswordUpdateDataModel(
					urlServer, organizaton.getName(), locale, organizationUserPasswordChangeSubject);
			DocumentContent subject = templateGenerator.generateDocument(dataModelSubject);

			// Génération du corps
			UserOrganizationPasswordUpdateDataModel dataModelBody = new UserOrganizationPasswordUpdateDataModel(
					urlServer, organizaton.getName(), locale, organizationUserPasswordChangeBody);
			DocumentContent body = templateGenerator.generateDocument(dataModelBody);

			// Création du model de courrier
			EMailDescription emailDescription = new EMailDescription(receivers,
					FileUtils.readFileToString(subject.getFile(), StandardCharsets.UTF_8), body);

			// Envoi du mail à tous les destinataires
			emailService.sendMailAndCatchException(emailDescription);

		} catch (GenerationModelNotFoundException | GenerationException | IOException e) {
			log.error("Erreur lors de l'envoi de mail de confirmation de changement de mot de passe", e);
		}
	}

	private String getAndCheckEmail(User user) {

		if (user.getLogin() == null || !user.getLogin().matches(UserOrganizationEmailHelper.EMAIL_PATTERN)) {
			log.error("Aucune adresse pour l'user d'uuid :  {}", user.getUuid());
			return null;
		}

		return user.getLogin();
	}
}
