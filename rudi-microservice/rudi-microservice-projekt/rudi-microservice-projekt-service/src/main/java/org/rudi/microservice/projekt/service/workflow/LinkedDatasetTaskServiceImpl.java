/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.workflow;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.impl.AbstractTaskServiceImpl;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.service.helper.linkeddataset.LinkedDatasetAssigmentHelper;
import org.rudi.microservice.projekt.service.helper.linkeddataset.LinkedDatasetWorkflowHelper;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * @author FNI18300
 *
 */
@Service
@Slf4j
public class LinkedDatasetTaskServiceImpl extends
		AbstractTaskServiceImpl<LinkedDatasetEntity, LinkedDataset, LinkedDatasetDao, LinkedDatasetWorkflowHelper, LinkedDatasetAssigmentHelper> {

	private final ProjectCustomDao projectCustomDao;

	public LinkedDatasetTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			LinkedDatasetDao assetDescriptionDao, LinkedDatasetWorkflowHelper assetDescriptionHelper,
			LinkedDatasetAssigmentHelper assigmentHelper, ProjectCustomDao projectCustomDao) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
		this.projectCustomDao = projectCustomDao;
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables, LinkedDatasetEntity assetDescriptionEntity) {
		ProjectEntity projectEntity = projectCustomDao.findProjectByLinkedDatasetUuid(assetDescriptionEntity.getUuid());
		if (projectEntity != null) {
			variables.put(ProjektWorkflowConstants.OWNER_PROJECT_UUID, projectEntity.getUuid());
		}

		variables.put(ProjektWorkflowConstants.DATASET_PRODUCER_UUID, assetDescriptionEntity.getDatasetOrganisationUuid());
	}

	@Override
	public String getProcessDefinitionKey() {
		return "linked-dataset-process";
	}

	@PostConstruct
	@Override
	public void loadBpmn() throws IOException {
		super.loadBpmn();
	}

}
