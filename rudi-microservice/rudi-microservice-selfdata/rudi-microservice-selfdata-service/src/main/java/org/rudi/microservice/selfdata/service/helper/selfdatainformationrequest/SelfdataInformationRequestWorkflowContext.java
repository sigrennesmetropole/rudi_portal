package org.rudi.microservice.selfdata.service.helper.selfdatainformationrequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.script.ScriptContext;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.bean.workflow.EMailData;
import org.rudi.facet.bpmn.bean.workflow.EMailDataModel;
import org.rudi.facet.bpmn.exception.InvalidDataException;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.AbstractWorkflowContext;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.rudi.facet.kaccess.bean.MatchingData;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.SelfdataContent;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.bean.OrganizationMember;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.selfdata.core.bean.MatchingDescription;
import org.rudi.microservice.selfdata.core.bean.MatchingField;
import org.rudi.microservice.selfdata.core.bean.NodeProviderInfo;
import org.rudi.microservice.selfdata.service.exception.MissingProviderException;
import org.rudi.microservice.selfdata.service.exception.ProviderApiException;
import org.rudi.microservice.selfdata.service.exception.UserNotFoundException;
import org.rudi.microservice.selfdata.service.helper.provider.SelfdataProviderHelper;
import org.rudi.microservice.selfdata.service.helper.selfdatamatchingdata.SelfdataMatchingDataHelper;
import org.rudi.microservice.selfdata.service.utils.SelfdataPairingUtils;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestStatus;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataTokenTupleEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Component(value = "selfdataInformationRequestWorkflowContext")
@Transactional(readOnly = true)
@Slf4j
public class SelfdataInformationRequestWorkflowContext extends AbstractWorkflowContext<SelfdataInformationRequestEntity, SelfdataInformationRequestDao, SelfdataInformationRequestAssigmentHelper> {

	@Value("${role.moderator:MODERATOR}")
	private String moderatorRole;

	@Value("${email.urlServer:}")
	private String urlServer;
	private final DatasetService datasetService;
	private final OrganizationHelper organizationHelper;
	private final SelfdataProviderHelper selfdataProviderHelper;
	private final SelfdataPairingUtils selfdataPairingUtils;
	private final SelfdataMatchingDataHelper selfdataMatchingDataHelper;

	protected SelfdataInformationRequestWorkflowContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			SelfdataInformationRequestDao assetDescriptionDao, SelfdataInformationRequestAssigmentHelper assignmentHelper,
			ACLHelper aclHelper, FormHelper formHelper, DatasetService datasetService, OrganizationHelper organizationHelper, SelfdataProviderHelper selfdataProviderHelper, SelfdataPairingUtils selfdataPairingUtils, SelfdataMatchingDataHelper selfdataMatchingDataHelper) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
		this.datasetService = datasetService;
		this.organizationHelper = organizationHelper;
		this.selfdataProviderHelper = selfdataProviderHelper;
		this.selfdataPairingUtils = selfdataPairingUtils;
		this.selfdataMatchingDataHelper = selfdataMatchingDataHelper;
	}

	@Transactional(readOnly = false)
	public void updateStatus(ScriptContext scriptContext, ExecutionEntity executionEntity, String statusValue,
			String selfdataInformationRequestStatusValue, String functionalStatusValue) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		log.debug("WkC - Update {} to status {}", processInstanceBusinessKey, statusValue);
		Status status = Status.valueOf(statusValue);
		SelfdataInformationRequestStatus assetStatus = SelfdataInformationRequestStatus.valueOf(selfdataInformationRequestStatusValue);
		if (processInstanceBusinessKey != null && status != null && functionalStatusValue != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			SelfdataInformationRequestEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			if (assetDescription != null) {
				assetDescription.setStatus(status);
				assetDescription.setSelfdataInformationRequestStatus(assetStatus);
				assetDescription.setFunctionalStatus(functionalStatusValue);
				assetDescription.setUpdatedDate(LocalDateTime.now());
				getAssetDescriptionDao().save(assetDescription);
				log.debug("WkC - Update {} to status {} done.", processInstanceBusinessKey, statusValue);
			} else {
				log.debug("WkC - Unkown {} skipped.", processInstanceBusinessKey);
			}
		} else {
			log.debug("WkC - Update {} to status {} skipped.", processInstanceBusinessKey, statusValue);
		}
	}

	/**
	 * Retourne la liste des moderateurs candidats pour la tâche
	 *
	 * @param scriptContext le context
	 * @return la liste des moderateurs qui voient la demande d'information soumise par l'utilisateur
	 */
	@SuppressWarnings("unused") // Utilisé par selfdata-information-request-process.bpmn20.xml
	public List<String> computeModerators(ScriptContext scriptContext, ExecutionEntity executionEntity,
			String subject, String body) {
		SelfdataInformationRequestEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
		log.debug("computePotentialModerators...");
		// On renvoie le rôle MODERATOR qui servira dans les scripts pour récuperer tous les membres ayant ce rôle
		List<String> list = new ArrayList<>();
		list.add(moderatorRole);
		sendEmailToUsersByRole(executionEntity, assetDescription, list, subject, body);
		return list;
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 *
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @return la liste des users par leur identifiant sec-username
	 */
	@SuppressWarnings("unused") // Utilisé par selfdata-information-request-process.bpmn20.xml
	public List<String> computePotentialProducersOwners(ScriptContext scriptContext, ExecutionEntity executionEntity,
			String subject, String body) {
		log.debug("computePotentialProducersOwners...");
		EMailData eMailData = null;
		// On Calcul les données de EmailData que si un subject et un body ont été
		// fournis
		if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body)) {
			eMailData = new EMailData(subject, body);
		}
		List<String> assignees = new ArrayList<>();
		List<String> assigneeEmails = new ArrayList<>();
		SelfdataInformationRequestEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
		if (assetDescription != null) {
			Metadata metadata = null;
			try { // Dataset exists ?
				metadata = datasetService.getDataset(assetDescription.getDatasetUuid());
				final var organization = metadata.getProducer();
				final var uuid = organization.getOrganizationId();
				Collection<OrganizationMember> members = organizationHelper.getOrganizationMembers(uuid);
				if (CollectionUtils.isNotEmpty(members)) {
					assignees = new ArrayList<>();
					for (OrganizationMember member : members) {
						final var user = getAclHelper().getUserByUUID(member.getUserUuid());
						if (user != null) {
							assignees.add(user.getLogin());
							assigneeEmails.add(lookupEMailAddress(user));
						} else {
							log.warn("Member with user uuid \"{}\" does not exist as user in ACL", member.getUserUuid());
						}
					}
				}
				if (log.isInfoEnabled()) {
					log.info("Assignees: {}", StringUtils.join(assignees, ", "));
				}
				if (eMailData != null) {
					sendEMail(executionEntity, assetDescription, eMailData, assigneeEmails, null);
				}
			} catch (Exception e) {
				log.warn("Failed to send email to " + assignees + " from " + assetDescription, e);
			}
		}
		return assignees;
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 *
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @return le type d'acces (API/OUT) d'un selfdata
	 */
	@SuppressWarnings("unused") // Utilisé par selfdata-information-request-process.bpmn20.xml
	public String getAccessType(ScriptContext scriptContext, ExecutionEntity executionEntity) {
		SelfdataInformationRequestEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
		SelfdataContent.SelfdataAccessEnum accessEnum = null;
		if (assetDescription != null) {
			Metadata metadata = null;
			try { // Dataset exists ?
				metadata = datasetService.getDataset(assetDescription.getDatasetUuid());
				accessEnum = metadata.getExtMetadata().getExtSelfdata().getExtSelfdataContent().getSelfdataAccess();
			} catch (Exception e) {
				log.warn("Failed to find dataset with uuid: " + assetDescription.getDatasetUuid(), e);
			}
		}
		return accessEnum == null ? "" : accessEnum.getValue();
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 *
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @return l'initiateur de la tâche
	 */
	@SuppressWarnings("unused") // Utilisé par selfdata-information-request-process.bpmn20.xml
	public List<String> getInitiator(ScriptContext scriptContext, ExecutionEntity executionEntity,
			String subject, String body) {
		SelfdataInformationRequestEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
		log.debug("getInitiator inside a list...");
		// On renvoie l'initiateur de la task
		List<String> list = new ArrayList<>();
		list.add(assetDescription.getInitiator());
		sendEmailToUser(executionEntity, assetDescription, list, subject, body);
		return list;
	}

	/**
	 * sendEmailToUser envoie un email à un User
	 *
	 * @param executionEntity
	 * @param assetDescription
	 * @param userList
	 * @param subject
	 * @param body
	 */
	private void sendEmailToUser(ExecutionEntity executionEntity, SelfdataInformationRequestEntity assetDescription, List<String> userList,
			String subject, String body) {
		EMailData eMailData = null;
		// On Calcul les données de EmailData que si un subject et un body ont été
		// fournis
		if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body)) {
			eMailData = new EMailData(subject, body);
		}
		List<String> assigneeEmails = new ArrayList<>();
		try {
			if (CollectionUtils.isNotEmpty(userList)) {
				for (String member : userList) {
					final var user = getAclHelper().getUserByLogin(member);
					if (user != null) {
						assigneeEmails.add(lookupEMailAddress(user));
					} else {
						log.warn("Member with user login \"{}\" does not exist as user in ACL", userList);
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("Assignees: {}", StringUtils.join(userList, ", "));
			}
			if (eMailData != null) {
				sendEMail(executionEntity, assetDescription, eMailData, assigneeEmails, null);
			}
		} catch (Exception e) {
			log.warn("Failed to send email to " + userList + " from " + assetDescription, e);
		}
	}

	/**
	 * sendEmailToUsersByRole envoie un email à un tous les users d'un rôle
	 *
	 * @param executionEntity
	 * @param assetDescription
	 * @param userRole
	 * @param subject
	 * @param body
	 */
	private void sendEmailToUsersByRole(ExecutionEntity executionEntity, SelfdataInformationRequestEntity assetDescription, List<String> userRole,
			String subject, String body) {
		EMailData eMailData = null;
		List<User> users;
		// On Calcul les données de EmailData que si un subject et un body ont été
		// fournis
		if (StringUtils.isNotEmpty(subject) && StringUtils.isNotEmpty(body)) {
			eMailData = new EMailData(subject, body);
		}
		List<String> assigneeEmails = new ArrayList<>();
		try {
			if (CollectionUtils.isNotEmpty(userRole)) {
				for (String role : userRole) {
					users = getAclHelper().searchUsers(role);
					if (users != null) {
						for (User user : users) {
							assigneeEmails.add(lookupEMailAddress(user));
						}
					} else {
						log.warn("Member with user with role \"{}\" does not exist as user in ACL", userRole);
					}
				}
			}
			if (log.isInfoEnabled()) {
				log.info("Assignees: {}", StringUtils.join(userRole, ", "));
			}
			if (eMailData != null) {
				sendEMail(executionEntity, assetDescription, eMailData, assigneeEmails, null);
			}
		} catch (Exception e) {
			log.warn("Failed to send email to user with role " + userRole + " from " + assetDescription, e);
		}
	}

	/**
	 * Fait l'appariement entre l'utilisateur, les données pivots saisies et le JDD selfdata
	 *
	 * @param scriptContext
	 * @param executionEntity
	 * @return token d'appariement pour un user ou null si l'utilisateur n'est pas dans le JDD
	 * @throws InvalidDataException
	 * @throws DataverseAPIException
	 */
	@Transactional(readOnly = false)
	@Nullable
	public String callMatchingDataService(ScriptContext scriptContext, ExecutionEntity executionEntity)
			throws InvalidDataException, DataverseAPIException, UserNotFoundException, AppServiceNotFoundException, AppServiceForbiddenException, AppServiceUnauthorizedException, MissingProviderException, ProviderApiException {
		// Verifier la présence du citoyen dans ACL
		SelfdataInformationRequestEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
		String citizenLogin = assetDescription.getInitiator();
		User user = this.getAclHelper().getUserByLogin(citizenLogin);
		if (user == null) {
			// Ne pas faire la demande d'appariement
			throw new UserNotFoundException("L'initiateur de la demande ne correspond à aucun utilisateur ACL");
		}
		// Préparer l'appel au noeud fournisseur : extraire la partie des données pivots dans toutes les données de formulaire dont on dispose
		Map<String, Object> informationRequestMap = this.getFormHelper().hydrateData(assetDescription.getData());
		final UUID datasetUuid = assetDescription.getDatasetUuid();
		final Metadata dataset = datasetService.getDataset(datasetUuid);
		final List<MatchingData> matchingDataList = selfdataPairingUtils.getMatchingDataList(dataset);
		List<MatchingField> matchingDataFields = selfdataPairingUtils.extractMatchingDataFromFormData(informationRequestMap, matchingDataList);
		// Dechiffrement des matchingData.
		selfdataMatchingDataHelper.decrypt(matchingDataFields);

		// Appeler sur le bouchon l'API d'appariemment
		NodeProviderInfo providerInfo;
		providerInfo = selfdataPairingUtils.getNodeProviderInfo(dataset);
		MatchingDescription matchingDescription;
		matchingDescription = selfdataProviderHelper.sendMatchingDataForPairing(providerInfo.getUrl(), datasetUuid, citizenLogin, matchingDataFields);
		boolean userPresent = matchingDescription != null;
		SelfdataTokenTupleEntity entitySaved = null;
		if (userPresent) {
			// Stockage du token
			entitySaved = selfdataPairingUtils.savePairingTokenForUser(UUID.fromString(matchingDescription.getToken()), user.getUuid(), datasetUuid, providerInfo.getUuid());
		}

		// Suppression des données pivots après traitement de la demande
		selfdataPairingUtils.removeAttachmentFromMatchingData(matchingDataList, informationRequestMap);
		assetDescription.setData(this.getFormHelper().deshydrateData(informationRequestMap));
		// MAJ de l'entity et renvoie du token ou null si utilisateur non présent dans JDD
		assetDescription.setUserPresent(userPresent);
		return userPresent ? entitySaved.getToken().toString() : null;
	}

	@Override
	protected void addEmailDataModelData(EMailDataModel<SelfdataInformationRequestEntity, SelfdataInformationRequestAssigmentHelper> eMailDataModel) {
		eMailDataModel.addData("urlServer", urlServer);
	}
}
