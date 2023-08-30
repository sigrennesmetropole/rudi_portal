/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.workflow;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.impl.AbstractTaskServiceImpl;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.service.helper.newdatasetrequest.NewDatasetRequestAssigmentHelper;
import org.rudi.microservice.projekt.service.helper.newdatasetrequest.NewDatasetRequestWorkflowHelper;
import org.rudi.microservice.projekt.storage.dao.newdatasetrequest.NewDatasetRequestDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectCustomDao;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class NewDatasetRequestTaskServiceImpl extends
		AbstractTaskServiceImpl<NewDatasetRequestEntity, NewDatasetRequest, NewDatasetRequestDao, NewDatasetRequestWorkflowHelper, NewDatasetRequestAssigmentHelper> {

	private final ProjectCustomDao projectCustomDao;

	public NewDatasetRequestTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			NewDatasetRequestDao assetDescriptionDao, NewDatasetRequestWorkflowHelper assetDescriptionHelper,
			NewDatasetRequestAssigmentHelper assigmentHelper, ProjectCustomDao projectCustomDao) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
		this.projectCustomDao = projectCustomDao;
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables, NewDatasetRequestEntity assetDescriptionEntity) {
		variables.put(ProjektWorkflowConstants.TITLE, assetDescriptionEntity.getTitle());

		ProjectEntity projectEntity = projectCustomDao
				.findProjectByNewDatasetRequestUuid(assetDescriptionEntity.getUuid());
		if (projectEntity != null) {
			variables.put(ProjektWorkflowConstants.OWNER_PROJECT_UUID, projectEntity.getUuid());
		}
	}

	@Override
	public String getProcessDefinitionKey() {
		return "new-dataset-request-process";
	}

	@PostConstruct
	@Override
	public void loadBpmn() throws IOException {
		super.loadBpmn();
	}

	@Override
	protected AbstractTaskServiceImpl<NewDatasetRequestEntity, NewDatasetRequest, NewDatasetRequestDao, NewDatasetRequestWorkflowHelper, NewDatasetRequestAssigmentHelper> lookupMe() {
		return ApplicationContext.getBean(NewDatasetRequestTaskServiceImpl.class);
	}

}
