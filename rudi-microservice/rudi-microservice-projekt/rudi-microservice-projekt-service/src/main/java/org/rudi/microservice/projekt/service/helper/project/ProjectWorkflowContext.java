/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.helper.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.script.ScriptContext;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.bpmn.core.bean.Task;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.facet.email.EMailService;
import org.rudi.facet.generator.text.impl.TemplateGeneratorImpl;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.service.helper.AbstractProjektWorkflowContext;
import org.rudi.microservice.projekt.service.mapper.LinkedDatasetMapper;
import org.rudi.microservice.projekt.service.mapper.NewDatasetRequestMapper;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
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

	private static final String PROJECT_ROUTE = "project";

	private static final String REUSE_ROUTE = "reuse";

	private final TaskService<NewDatasetRequest> newDatasetRequestTaskService;

	private final NewDatasetRequestMapper newDatasetRequestMapper;

	private final TaskService<LinkedDataset> linkedDatasetTaskService;

	private final LinkedDatasetMapper linkedDatasetMapper;

	public ProjectWorkflowContext(EMailService eMailService, TemplateGeneratorImpl templateGenerator,
			ProjectDao assetDescriptionDao, ProjectAssigmentHelper assignmentHelper, ACLHelper aclHelper,
			FormHelper formHelper, TaskService<NewDatasetRequest> newDatasetRequestTaskService,
			NewDatasetRequestMapper newDatasetRequestMapper, TaskService<LinkedDataset> linkedDatasetTaskService,
			LinkedDatasetMapper linkedDatasetMapper) {
		super(eMailService, templateGenerator, assetDescriptionDao, assignmentHelper, aclHelper, formHelper);
		this.newDatasetRequestTaskService = newDatasetRequestTaskService;
		this.newDatasetRequestMapper = newDatasetRequestMapper;
		this.linkedDatasetTaskService = linkedDatasetTaskService;
		this.linkedDatasetMapper = linkedDatasetMapper;
	}

	@Transactional(readOnly = false)
	public void updateStatus(ScriptContext scriptContext, ExecutionEntity executionEntity, String statusValue,
			String projectStatusValue, String functionalStatusValue) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		log.debug("WkC - Update {} to status {}", processInstanceBusinessKey, statusValue);
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

	public String computeType(ScriptContext scriptContext, ExecutionEntity executionEntity) {
		String result = REUSE_ROUTE;

		// Une réutlisation est un projet qui n'a pas de desiredSupport (desiredSupportList.isEmpty())
		// Un projet qui ne nécessite pas de support (desiredSupport selectionné == AUCUN) est un projet quand même
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			return assetDescription.isAReuse() ? REUSE_ROUTE : PROJECT_ROUTE;
		}
		return result;
	}

	/**
	 * Calcul des potentiels owners pour le gestionnaire de projet et le modérateur
	 *
	 * @param scriptContext
	 * @param executionEntity
	 * @return
	 */
	public List<String> computePotentialProjectManagerAndModerator(ScriptContext scriptContext,
			ExecutionEntity executionEntity) {
		List<String> result = new ArrayList<>();
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			if (assetDescription != null) {
				// pour l'instant on ne met que le demandeur
				// TODO il faudra pendre en compte les organizations et donc les managers des organisations
				result.add(assetDescription.getInitiator());
				getAssignmentHelper().computeAssignee(assetDescription, getAssignmentHelper().getModeratorRoleCode());
			}
		}
		return result;
	}

	public void startSubProcess(ExecutionEntity executionEntity) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			ProjectEntity assetDescription = getAssetDescriptionDao().findByUuid(uuid);
			startSubProcessNewDatasetRequests(assetDescription);
			startProcessLinkedDatasets(assetDescription);
		}
	}

	private void startProcessLinkedDatasets(ProjectEntity project) {
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
}
