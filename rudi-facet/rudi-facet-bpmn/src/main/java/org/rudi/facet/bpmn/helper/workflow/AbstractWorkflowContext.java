/**
 * 
 */
package org.rudi.facet.bpmn.helper.workflow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.script.ScriptContext;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.common.core.DocumentContent;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.bean.workflow.EMailData;
import org.rudi.facet.bpmn.bean.workflow.EMailDataModel;
import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.email.exception.EMailException;
import org.rudi.facet.email.model.EMailDescription;
import org.rudi.facet.generator.exception.GenerationException;
import org.rudi.facet.generator.exception.GenerationModelNotFoundException;
import org.rudi.facet.generator.text.impl.TemplateGeneratorConstants;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * @author FNI18300
 *
 */
@Transactional(readOnly = true)
public abstract class AbstractWorkflowContext<E extends AssetDescriptionEntity, D extends AssetDescriptionDao<E>, A extends AssignmentHelper<E>> {

	private static final String WK_C_FAILED_TO_SEND_MAIL_FOR = "WkC - Failed to send mail for ";

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWorkflowContext.class);

	public static final String COMPUTE_POTENTIAL_OWNERS = "computePotentialOwners";

	public static final String COMPUTE_HUMAN_PERFORMER = "computeHumanPerformer";

	@Getter(value = AccessLevel.PROTECTED)
	private final EMailService eMailService;

	@Getter(value = AccessLevel.PROTECTED)
	private final TemplateGeneratorImpl templateGenerator;

	@Getter(value = AccessLevel.PROTECTED)
	private final A assignmentHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final D assetDescriptionDao;

	@Getter(value = AccessLevel.PROTECTED)
	private final ACLHelper aclHelper;

	@Getter(value = AccessLevel.PROTECTED)
	private final FormHelper formHelper;

	protected AbstractWorkflowContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			D assetDescriptionDao, A assignmentHelper, ACLHelper aclHelper, FormHelper formHelper) {
		super();
		this.eMailService = eMailService;
		this.templateGenerator = templateGenerator;
		this.assetDescriptionDao = assetDescriptionDao;
		this.assignmentHelper = assignmentHelper;
		this.aclHelper = aclHelper;
		this.formHelper = formHelper;
	}

	/**
	 * Méthode utilitaire de log
	 * 
	 * @param message
	 */
	public void info(String message) {
		LOGGER.info("WkC - {}", message);
	}

	/**
	 * Méthode de mise à jour de l'état de l'asset associé, avec prise en compte du status fonctionnel
	 *
	 * @param scriptContext   le context du script
	 * @param executionEntity le context d'execution
	 * @param statusValue     l'état cible
	 */
	@Transactional(readOnly = false)
	public void updateStatus(ScriptContext scriptContext, ExecutionEntity executionEntity, String statusValue,
			String functionalStatusValue) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		LOGGER.debug("WkC - Update {} to status {}", processInstanceBusinessKey, statusValue);
		Status status = Status.valueOf(statusValue);
		if (processInstanceBusinessKey != null && status != null && functionalStatusValue != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			E assetDescription = assetDescriptionDao.findByUuid(uuid);
			if (assetDescription != null) {
				assetDescription.setStatus(status);
				assetDescription.setFunctionalStatus(functionalStatusValue);
				assetDescription.setUpdatedDate(LocalDateTime.now());
				assetDescriptionDao.save(assetDescription);
				LOGGER.debug("WkC - Update {} to status {} done.", processInstanceBusinessKey, statusValue);
			} else {
				LOGGER.debug("WkC - Unkown {} skipped.", processInstanceBusinessKey);
			}
		} else {
			LOGGER.debug("WkC - Update {} to status {} skipped.", processInstanceBusinessKey, statusValue);
		}
	}

	/**
	 * Méthode de mise à jour de l'état de l'asset associé
	 * 
	 * @param scriptContext   le context du script
	 * @param executionEntity le context d'execution
	 * @param statusValue     l'état cible
	 */
	@Transactional(readOnly = false)
	public void updateStatus(ScriptContext scriptContext, ExecutionEntity executionEntity, String statusValue) {
		updateStatus(scriptContext, executionEntity, statusValue, statusValue);
	}

	/**
	 * Envoi de courriel
	 * 
	 * @param scriptContext   le context du script
	 * @param executionEntity le context d'execution
	 * @param eMailData
	 * @param logins
	 */
	public void sendEMail(ScriptContext scriptContext, ExecutionEntity executionEntity, EMailData eMailData,
			List<String> logins) {
		LOGGER.debug("Send email to dedicated emails {}...", logins);
		try {
			AssetDescriptionEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
			if (assetDescription != null && eMailData != null) {
				sendEMail(executionEntity, assetDescription, eMailData, lookupEMailAddresses(lookupUsers(logins)));
			}
		} catch (Exception e) {
			LOGGER.warn(WK_C_FAILED_TO_SEND_MAIL_FOR + executionEntity.getProcessDefinitionKey(), e);
		}
	}

	/**
	 * Envoi de courriel
	 * 
	 * @param scriptContext   le context du script
	 * @param executionEntity le context d'execution
	 * @param eMailData
	 */
	public void sendEMailToInitiator(ScriptContext scriptContext, ExecutionEntity executionEntity,
			EMailData eMailData) {
		LOGGER.debug("Send email to initiator...");
		try {
			AssetDescriptionEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
			if (assetDescription != null && eMailData != null) {
				sendEMail(executionEntity, assetDescription, eMailData,
						Arrays.asList(lookupEMailAddress(lookupUser(assetDescription.getInitiator()))));
			}
		} catch (Exception e) {
			LOGGER.warn(WK_C_FAILED_TO_SEND_MAIL_FOR + executionEntity.getProcessDefinitionKey(), e);
		}
	}

	/**
	 * Envoi de courriel
	 * 
	 * @param scriptContext   le context du script
	 * @param executionEntity le context d'execution
	 * @param eMailData
	 * @param roleCode        le code d'un rôle destinataire
	 */
	public void sendEMailToRole(ScriptContext scriptContext, ExecutionEntity executionEntity, EMailData eMailData,
			String roleCode) {
		LOGGER.debug("Send email to role...");
		try {
			AssetDescriptionEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
			List<User> users = aclHelper.searchUsers(roleCode);
			if (assetDescription != null && eMailData != null && CollectionUtils.isNotEmpty(users)) {
				sendEMail(executionEntity, assetDescription, eMailData, lookupEMailAddresses(users));
			}
		} catch (Exception e) {
			LOGGER.warn(WK_C_FAILED_TO_SEND_MAIL_FOR + executionEntity.getProcessDefinitionKey(), e);
		}
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 * 
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @param roleName        le rôle rechercher
	 * @return la liste des users par leur identifiant sec-username
	 */
	public List<String> computePotentialOwners(ScriptContext scriptContext, ExecutionEntity executionEntity,
			String roleName, String subject, String body) {
		LOGGER.debug("computePotentialOwners...");
		EMailData eMailData = null;
		// On Calcul les données de EmailData que si un subject et un body ont été
		// fournis
		if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body)) {
			eMailData = new EMailData(subject, body);
		}
		return computePotentialOwners(scriptContext, executionEntity, roleName, eMailData);
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 * 
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @param roleName        le rôle rechercher
	 * @return la liste des users par leur identifiant sec-username
	 */
	public List<String> computePotentialOwners(ScriptContext scriptContext, ExecutionEntity executionEntity,
			String roleName, EMailData eMailData) {
		LOGGER.debug("computePotentialOwners...");
		List<String> assignees = null;
		E assetDescription = lookupAssetDescriptionEntity(executionEntity);
		if (assetDescription != null) {
			assignees = assignmentHelper.computeAssignees(assetDescription, roleName);
			try {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Assignees: {}", StringUtils.join(assignees, ", "));
				}
				// Ici il faut calcule le contenue de recipients
				// On envoie un mail à tous les potentialOwners si demandé
				if (eMailData != null) {
					sendEMail(executionEntity, assetDescription, eMailData,
							lookupEMailAddresses(lookupUsers(assignees)), roleName);
				}
			} catch (Exception e) {
				LOGGER.warn("Failed to send email to " + assignees + " from " + assetDescription, e);
			}
		}
		return assignees;
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 * 
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @param roleName        le rôle rechercher
	 * @return la liste des users par leur identifiant sec-username
	 */
	public String computeHumanPerformer(ScriptContext scriptContext, ExecutionEntity executionEntity, String roleName,
			EMailData eMailData) {
		LOGGER.debug("computeHumanPerformer...");
		String assignee = null;
		E assetDescription = lookupAssetDescriptionEntity(executionEntity);
		if (assetDescription != null) {
			assignee = assignmentHelper.computeAssignee(assetDescription, roleName);
			try {
				LOGGER.info("Assignees: {}", assignee);
				// Ici il faut calcule le contenue de result
				sendEMail(executionEntity, assetDescription, eMailData,
						Arrays.asList(lookupEMailAddress(lookupUser(assignee))));
			} catch (Exception e) {
				LOGGER.warn("Failed to send email to " + assignee + " from " + assetDescription, e);
			}
		}
		return assignee;
	}

	public String computeHumanPerformer(ScriptContext scriptContext, ExecutionEntity executionEntity, String roleName,
			String subject, String body) {
		LOGGER.debug("computeHumanPerformer...");
		EMailData eMailData = new EMailData(subject, body);
		return computeHumanPerformer(scriptContext, executionEntity, roleName, eMailData);
	}

	/**
	 * Calcule des groupes
	 * 
	 * @param scriptContext   le context
	 * @param executionEntity l'exécution
	 * @param roleCodes       la liste des noms des rôles
	 * @return
	 */
	public List<String> computeGroups(ScriptContext scriptContext, ExecutionEntity executionEntity,
			String... roleCodes) {
		return Arrays.asList(roleCodes);
	}

	protected E lookupAssetDescriptionEntity(ExecutionEntity executionEntity) {
		E result = null;
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			result = assetDescriptionDao.findByUuid(uuid);
		}
		if (result == null) {
			LOGGER.warn("WkC - No target for {}", processInstanceBusinessKey);
		}
		return result;
	}

	protected void sendEMail(ExecutionEntity executionEntity, AssetDescriptionEntity assetDescription,
			EMailData eMailData, List<String> emailRecipients)
			throws EMailException, IOException, GenerationModelNotFoundException, GenerationException {
		sendEMail(executionEntity, assetDescription, eMailData, emailRecipients, null);
	}

	protected void sendEMail(ExecutionEntity executionEntity, AssetDescriptionEntity assetDescription,
			EMailData eMailData, List<String> emailRecipients, String roleName)
			throws EMailException, IOException, GenerationModelNotFoundException, GenerationException {
		if (eMailData != null && CollectionUtils.isNotEmpty(emailRecipients)) {
			for (String emailRecipient : emailRecipients) {
				if (StringUtils.isNotEmpty(emailRecipient)) {
					LOGGER.info("Send mail to {}", emailRecipient);
					EMailDescription emailDescription = new EMailDescription();
					emailDescription
							.setSubject(generateSubject(executionEntity, assetDescription, eMailData, roleName));
					emailDescription.addTo(emailRecipient);
					emailDescription.setHtml(true);
					emailDescription.setBody(generateEMailBody(executionEntity, assetDescription, eMailData, roleName));
					eMailService.sendMail(emailDescription);
				}
			}
		}
	}

	protected User lookupUser(String login) {
		return aclHelper.getUserByLogin(login);
	}

	protected List<User> lookupUsers(List<String> logins) {
		List<User> users = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(logins)) {
			for (String login : logins) {
				User user = lookupUser(login);
				if (user != null) {
					users.add(user);
				}
			}
		}
		return users;
	}

	protected List<String> lookupEMailAddresses(List<User> users) {
		List<String> emails = new ArrayList<>();
		for (User user : users) {
			String email = lookupEMailAddress(user);
			if (StringUtils.isNotEmpty(email)) {
				emails.add(email);
			}
		}
		return emails;
	}

	protected String lookupEMailAddress(User user) {
		return aclHelper.lookupEMailAddress(user);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected EMailDataModel<E, A> createEmailDataModel(ExecutionEntity executionEntity,
			AssetDescriptionEntity assetDescription, String emailPart, String roleName) {
		Locale l = Locale.FRENCH;
		EMailDataModel eMailDataModel = new EMailDataModel(assignmentHelper, executionEntity, assetDescription,
				formHelper, roleName, l, emailPart);
		addEmailDataModelData(eMailDataModel);
		return eMailDataModel;
	}

	/**
	 * Point d'extension pour l'alimentation du datamodel des emails
	 * 
	 * @param eMailDataModel
	 */
	protected void addEmailDataModelData(EMailDataModel<E, A> eMailDataModel) {

	}

	protected String generateSubject(ExecutionEntity executionEntity, AssetDescriptionEntity assetDescription,
			EMailData eMailData, String roleName)
			throws GenerationModelNotFoundException, GenerationException, IOException {
		String subject = "No subject";
		if (eMailData.hasSubject()) {
			if (eMailData.isSubjectFile()) {
				subject = eMailData.getSubjectFile();
			} else {
				subject = eMailData.getSubject();
				if (!subject.startsWith(TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX)) {
					subject = TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX + subject;
				}
			}

			EMailDataModel<E, A> eMailDataModel = createEmailDataModel(executionEntity, assetDescription, subject,
					roleName);

			eMailDataModel.addAllData(eMailData.getData());

			DocumentContent subjectContent = templateGenerator.generateDocument(eMailDataModel);
			subject = FileUtils.readFileToString(subjectContent.getFile(), StandardCharsets.UTF_8);
		}
		return subject;
	}

	protected DocumentContent generateEMailBody(ExecutionEntity executionEntity,
			AssetDescriptionEntity assetDescription, EMailData eMailData, String roleName)
			throws GenerationModelNotFoundException, GenerationException, IOException {
		if (eMailData.hasBody()) {
			String bodyTemplate = null;
			if (eMailData.isBodyFile()) {
				bodyTemplate = eMailData.getBodyFile();
			} else {
				bodyTemplate = eMailData.getBody();
				if (!bodyTemplate.startsWith(TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX)) {
					bodyTemplate = TemplateGeneratorConstants.STRING_TEMPLATE_LOADER_PREFIX + bodyTemplate;
				}
			}

			EMailDataModel<E, A> eMailDataModel = createEmailDataModel(executionEntity, assetDescription, bodyTemplate,
					roleName);

			eMailDataModel.addAllData(eMailData.getData());

			return templateGenerator.generateDocument(eMailDataModel);
		} else {
			return null;
		}
	}
}
