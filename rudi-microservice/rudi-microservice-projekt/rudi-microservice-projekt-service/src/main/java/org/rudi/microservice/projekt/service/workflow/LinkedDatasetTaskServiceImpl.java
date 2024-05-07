/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.workflow;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.FormService;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.impl.AbstractTaskServiceImpl;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.service.helper.ProjektAuthorisationHelper;
import org.rudi.microservice.projekt.service.helper.linkeddataset.LinkedDatasetAssigmentHelper;
import org.rudi.microservice.projekt.service.helper.linkeddataset.LinkedDatasetWorkflowHelper;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 */
@Service
public class LinkedDatasetTaskServiceImpl extends
		AbstractTaskServiceImpl<LinkedDatasetEntity, LinkedDataset, LinkedDatasetDao, LinkedDatasetWorkflowHelper, LinkedDatasetAssigmentHelper> {

	public static final String PROCESS_DEFINITION_ID = "linked-dataset-process";

	private final ProjectCustomDao projectCustomDao;
	private final FormService formService;

	@Autowired
	private ProjektAuthorisationHelper projektAuthorisationHelper;

	public LinkedDatasetTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			LinkedDatasetDao assetDescriptionDao, LinkedDatasetWorkflowHelper assetDescriptionHelper,
			LinkedDatasetAssigmentHelper assigmentHelper, ProjectCustomDao projectCustomDao, FormService formService) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
		this.projectCustomDao = projectCustomDao;
		this.formService = formService;
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables, LinkedDatasetEntity assetDescriptionEntity) {
		ProjectEntity projectEntity = projectCustomDao.findProjectByLinkedDatasetUuid(assetDescriptionEntity.getUuid());
		if (projectEntity != null) {
			variables.put(ProjektWorkflowConstants.OWNER_PROJECT_UUID, projectEntity.getUuid());
		}

		variables.put(ProjektWorkflowConstants.DATASET_PRODUCER_UUID,
				assetDescriptionEntity.getDatasetOrganisationUuid());
	}

	@Override
	public String getProcessDefinitionKey() {
		return PROCESS_DEFINITION_ID;
	}

	@PostConstruct
	@Override
	public void loadBpmn() throws IOException {
		super.loadBpmn();
		formService.createOrUpdateAllSectionAndFormDefinitions();
	}

	@Override
	protected AbstractTaskServiceImpl<LinkedDatasetEntity, LinkedDataset, LinkedDatasetDao, LinkedDatasetWorkflowHelper, LinkedDatasetAssigmentHelper> lookupMe() {
		return ApplicationContext.getBean(LinkedDatasetTaskServiceImpl.class);
	}

	/**
	 * @param assetDescriptionEntity linkedDatasetEntity
	 */
	@Override
	protected void updateAssetCreation(LinkedDatasetEntity assetDescriptionEntity) {
		super.updateAssetCreation(assetDescriptionEntity);

		// Réécriture de l'initiator : initator de la demande de jeux de donnée = owner du projet associé
		ProjectEntity projectEntity = projectCustomDao.findProjectByLinkedDatasetUuid(assetDescriptionEntity.getUuid());
		if (projectEntity != null) {
			User user = getAssignmentHelper().getUserByUuid(projectEntity.getOwnerUuid());
			if (user != null) {
				assetDescriptionEntity.setInitiator(user.getLogin());
			}
		}
	}

	@Override
	protected void checkRightsOnInitEntity(LinkedDatasetEntity assetDescriptionEntity) throws IllegalArgumentException {

		ProjectEntity projectEntity = projectCustomDao.findProjectByLinkedDatasetUuid(assetDescriptionEntity.getUuid());
		if (projectEntity != null) {
			try {
				projektAuthorisationHelper.checkRightsAdministerProject(projectEntity);
			} catch (GetOrganizationMembersException | MissingParameterException | AppServiceUnauthorizedException e) {
				throw new IllegalArgumentException(
						"Erreur lors de la vérification des droits pour le traitement de la tache de linkeddataset", e);
			}
		}
	}

}
