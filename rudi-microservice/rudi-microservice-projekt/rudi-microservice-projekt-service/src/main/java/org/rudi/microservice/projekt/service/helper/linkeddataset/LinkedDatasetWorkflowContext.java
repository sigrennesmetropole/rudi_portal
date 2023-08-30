/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.linkeddataset;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.script.ScriptContext;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.bean.workflow.EMailData;
import org.rudi.facet.bpmn.bean.workflow.EMailDataModel;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.bean.OrganizationMember;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.microservice.projekt.service.helper.AbstractProjektWorkflowContext;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 */
@Component(value = "linkedDatasetWorkflowContext")
@Transactional(readOnly = true)
@Slf4j
public class LinkedDatasetWorkflowContext
		extends AbstractProjektWorkflowContext<LinkedDatasetEntity, LinkedDatasetDao, LinkedDatasetAssigmentHelper> {

	private final DatasetService datasetService;

	private final OrganizationHelper organizationHelper;

	private final ProjectCustomDao projectCustomDao;

	public LinkedDatasetWorkflowContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			LinkedDatasetDao assetDescriptionDao, LinkedDatasetAssigmentHelper assignmentHelper, ACLHelper aclHelper,
			FormHelper formHelper, DatasetService datasetService, OrganizationHelper organizationHelper,
			ProjectCustomDao projectCustomDao) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
		this.datasetService = datasetService;
		this.organizationHelper = organizationHelper;
		this.projectCustomDao = projectCustomDao;
	}

	@Transactional(readOnly = false)
	public void updateStatus(ScriptContext scriptContext, ExecutionEntity executionEntity, String statusValue,
			String linkedDatasetStatusValue, String functionalStatusValue) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		log.debug("WkC - Update {} to status {}", processInstanceBusinessKey, statusValue);
		Status status = Status.valueOf(statusValue);
		LinkedDatasetStatus assetStatus = LinkedDatasetStatus.valueOf(linkedDatasetStatusValue);
		if (processInstanceBusinessKey != null && status != null && functionalStatusValue != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			LinkedDatasetEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			if (assetDescription != null) {
				assetDescription.setStatus(status);
				assetDescription.setLinkedDatasetStatus(assetStatus);
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
	 * Retourne la liste des users candidats pour la tâche
	 *
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @return la liste des users par leur identifiant sec-username
	 */
	@SuppressWarnings("unused") // Utilisé par linked-dataset-process.bpmn20.xml
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
		LinkedDatasetEntity assetDescription = lookupAssetDescriptionEntity(executionEntity);
		if (assetDescription != null) {
			Metadata metadata = null;
			try { // Dataset exists ?
				metadata = datasetService.getDataset(assetDescription.getDatasetUuid());
				final var organization = metadata.getProducer();
				final var uuid = organization.getOrganizationId();
				Collection<OrganizationMember> members = organizationHelper.getOrganizationMembers(uuid);
				if (CollectionUtils.isNotEmpty(members)) {
					computePotentialProducersOwnersAssigneesGestion(members, assignees, assigneeEmails);
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

	@Override
	protected void addEmailDataModelData(
			EMailDataModel<LinkedDatasetEntity, LinkedDatasetAssigmentHelper> eMailDataModel) {
		super.addEmailDataModelData(eMailDataModel);
		ProjectEntity projectEntity = projectCustomDao
				.findProjectByLinkedDatasetUuid(eMailDataModel.getAssetDescription().getUuid());
		if (projectEntity != null) {
			eMailDataModel.addData("project", projectEntity);
		}

		try {
			org.rudi.facet.organization.bean.Organization producer = organizationHelper.getOrganization(eMailDataModel.getAssetDescription().getDatasetOrganisationUuid());
			if (producer != null) {
				eMailDataModel.addData("producer", producer);
			}
		} catch (GetOrganizationException goe) {
			log.error("Error when retrieving producer infos", goe);
		}
	}

	private void computePotentialProducersOwnersAssigneesGestion(Collection<OrganizationMember> members, List<String> assignees, List<String> assigneeEmails){
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

}
