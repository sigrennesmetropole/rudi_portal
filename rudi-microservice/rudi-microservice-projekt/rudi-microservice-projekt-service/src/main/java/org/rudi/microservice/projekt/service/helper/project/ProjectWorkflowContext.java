/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.script.ScriptContext;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.bean.workflow.EMailData;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.service.helper.AbstractProjektWorkflowContext;
import org.rudi.microservice.projekt.service.mapper.LinkedDatasetMapper;
import org.rudi.microservice.projekt.service.mapper.NewDatasetRequestMapper;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
import org.rudi.microservice.projekt.storage.entity.OwnerType;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 */
@Component(value = "projectWorkflowContext")
@Transactional(readOnly = true)
@Slf4j
public class ProjectWorkflowContext
		extends AbstractProjektWorkflowContext<ProjectEntity, ProjectDao, ProjectAssigmentHelper> {

	private final TaskService<NewDatasetRequest> newDatasetRequestTaskService;

	private final NewDatasetRequestMapper newDatasetRequestMapper;

	private final TaskService<LinkedDataset> linkedDatasetTaskService;

	private final LinkedDatasetMapper linkedDatasetMapper;

	private final ACLHelper aclHelper;

	private final OrganizationHelper organizationHelper;

	public ProjectWorkflowContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			ProjectDao assetDescriptionDao, ProjectAssigmentHelper assignmentHelper, ACLHelper aclHelper,
			FormHelper formHelper, TaskService<NewDatasetRequest> newDatasetRequestTaskService,
			NewDatasetRequestMapper newDatasetRequestMapper, TaskService<LinkedDataset> linkedDatasetTaskService,
			LinkedDatasetMapper linkedDatasetMapper, ACLHelper aclHelper1, OrganizationHelper organizationHelper) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
		this.newDatasetRequestTaskService = newDatasetRequestTaskService;
		this.newDatasetRequestMapper = newDatasetRequestMapper;
		this.linkedDatasetTaskService = linkedDatasetTaskService;
		this.linkedDatasetMapper = linkedDatasetMapper;
		this.aclHelper = aclHelper1;
		this.organizationHelper = organizationHelper;
	}

	@Transactional(readOnly = true)
	public String getReutilisationStatus(ScriptContext scriptContext, ExecutionEntity executionEntity) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		String reutilisationStatusLabel = StringUtils.EMPTY;
		log.debug("WkC - Get status of reuse {}", processInstanceBusinessKey);
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			if (assetDescription != null) {
				reutilisationStatusLabel = assetDescription.getReutilisationStatus().getLabel();
				log.debug("WkC - Status of reuse {} is {}.", processInstanceBusinessKey, reutilisationStatusLabel);
			} else {
				log.debug("WkC - Unknown {} skipped.", processInstanceBusinessKey);
			}
		} else {
			log.debug("WkC - Get reuse status of {} skipped.", processInstanceBusinessKey);
		}
		return reutilisationStatusLabel;
	}

	@Transactional(readOnly = false)
	@SuppressWarnings("unused") // Utilisé par project-process.bpmn20.xml
	public void updateStatus(ScriptContext scriptContext, ExecutionEntity executionEntity, String statusValue,
			String projectStatusValue, String functionalStatusValue) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		log.debug("WkC - Update {} to status {}, {}, {}", processInstanceBusinessKey, statusValue, projectStatusValue,
				functionalStatusValue);
		Status status = Status.valueOf(statusValue);
		ProjectStatus projectStatus = ProjectStatus.valueOf(projectStatusValue);
		if (processInstanceBusinessKey != null && status != null && functionalStatusValue != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			if (assetDescription != null) {
				assetDescription.setStatus(status);
				assetDescription.setProjectStatus(projectStatus);
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
	 * Calcul des potentiels owners pour le gestionnaire de projet et le modérateur
	 *
	 * @param scriptContext contexte
	 * @param executionEntity entity
	 * @return List<String> liste des logins des owners potentiels
	 */
	@SuppressWarnings("unused") // Utilisé par project-process.bpmn20.xml
	public List<String> computePotentialProjectOwners(ScriptContext scriptContext, ExecutionEntity executionEntity) {
		List<String> result = new ArrayList<>();
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			if (assetDescription != null) {

				// Ajout du demandeur
				result.add(assetDescription.getInitiator());

				// Ajout de tous les membres de l'organisation
				CollectionUtils.addAll(result,
						getAssignmentHelper().computeOrganizationMembersLogins(assetDescription.getOwnerUuid()));

				if (log.isInfoEnabled()) {
					log.info("Assignees: {}", StringUtils.join(result, ", "));
				}
			}
		}
		return result;
	}

	/**
	 *	Envoit un mail au porteur d'un projet pour les notifier de la réponse du MODERATOR
	 * 		- Owner de type organisation : envoie un mail à tous les membres de l'organisation
	 *
	 * @param context le context
	 * @param executionEntity entité de l'éxécution, ici projet
	 * @param eMailData email à envoyer
	 */
	@SuppressWarnings("unused") // Utilisé par project-process.bpmn20.xml
	public void sendEmailToProjectOwner(ScriptContext context, ExecutionEntity executionEntity, EMailData eMailData){
		List<String> assigneesEmails = new ArrayList<>();
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		try{
			if (processInstanceBusinessKey != null) {

				UUID uuid = UUID.fromString(processInstanceBusinessKey);
				ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);

				if (assetDescription != null) {

					if(assetDescription.getOwnerType().equals(OwnerType.ORGANIZATION)){
						List<User> users = getAssignmentHelper().computeOrganizationMembers(assetDescription.getOwnerUuid());

						CollectionUtils.addAll(
								assigneesEmails,
								aclHelper.lookupEmailAddresses(users)
						);
					}
					else {
						assigneesEmails.add(Objects.requireNonNull(
								aclHelper.getUserByUUID(assetDescription.getOwnerUuid())).getLogin()
						);
					}
				}

				if (eMailData != null && CollectionUtils.isNotEmpty(assigneesEmails)){
					sendEMail(executionEntity, assetDescription, eMailData, assigneesEmails, null);
				}
			}
		}
		catch (Exception e){
			log.warn("Failed to send email to owner");
		}

	}

	@SuppressWarnings("unused") // Utilisé par project-process.bpmn20.xml
	public void startSubProcess(ExecutionEntity executionEntity) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			startSubProcessNewDatasetRequests(assetDescription);
			startSubProcessLinkedDatasets(assetDescription);
		}
	}

	private void startSubProcessLinkedDatasets(ProjectEntity project) {
		if (CollectionUtils.isNotEmpty(project.getLinkedDatasets())) {
			for (LinkedDatasetEntity linkedDataset : project.getLinkedDatasets()) {
				if (linkedDataset.getDatasetConfidentiality() == DatasetConfidentiality.RESTRICTED) {
					try {
						Task t = linkedDatasetTaskService.createDraft(linkedDatasetMapper.entityToDto(linkedDataset));
						linkedDatasetTaskService.startTask(t);
					} catch (Exception e) {
						log.error("Failed to start workflow for linkedDataset:" + linkedDataset);
					}
				}
			}
		}
	}

	private void startSubProcessNewDatasetRequests(ProjectEntity project) {
		if (CollectionUtils.isNotEmpty(project.getDatasetRequests())) {
			for (NewDatasetRequestEntity newDatasetRequest : project.getDatasetRequests()) {
				try {
					Task t = newDatasetRequestTaskService
							.createDraft(newDatasetRequestMapper.entityToDto(newDatasetRequest));
					newDatasetRequestTaskService.startTask(t);
				} catch (Exception e) {
					log.error("Failed to start workflow for newDatasetRequest:" + newDatasetRequest);
				}
			}
		}
	}

	/**
	 *
	 * @param context context
	 * @param executionEntity entity - ici project
	 * @return le nom du project owner ou de l'organisation owner du projet
	 */
	@SuppressWarnings("unused") // Utilisé par project-process.bpmn20.xml
	public String computeProjectOwnerName(ScriptContext context, ExecutionEntity executionEntity) {
		String result = null;
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			if (assetDescription != null) {
				if(assetDescription.getOwnerType().equals(OwnerType.ORGANIZATION)){
					result = computeProjectOwnerNameOrganization(assetDescription);
				}
				else {
					result = computeProjectOwnerNameUser(assetDescription);
				}
			}

		}
		return result;
	}

	private String computeProjectOwnerNameOrganization(ProjectEntity assetDescription){
		String result = null;
		try {
			Organization organization = organizationHelper.getOrganization(assetDescription.getOwnerUuid());
			result = organization != null ? organization.getName() : null;
		}catch (Exception e){
			log.error("Erreur lors de la récupéation de l'organisation owner du projet {} : {}", assetDescription.getTitle(), e);
		}

		return result;
	}

	private String computeProjectOwnerNameUser(ProjectEntity assetDescription){
		String result = null;
		User owner = aclHelper.getUserByUUID(assetDescription.getOwnerUuid());
		if(owner != null){
			result = String.format("%s %s", owner.getFirstname(), owner.getLastname());
		}
		else {
			log.error("L'UUID renseignée n'est pas rattachée à un utilisateur RUDI.");
		}
		return result;
	}


}
