/**
 * RUDI Portail
 */
package org.rudi.microservice.projekt.service.workflow;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.activiti.engine.ProcessEngine;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.bpmn.helper.workflow.BpmnHelper;
import org.rudi.facet.bpmn.service.InitializationService;
import org.rudi.facet.bpmn.service.impl.AbstractTaskServiceImpl;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.helper.project.ProjectAssigmentHelper;
import org.rudi.microservice.projekt.service.helper.project.ProjectWorkflowHelper;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class ProjectTaskServiceImpl extends
		AbstractTaskServiceImpl<ProjectEntity, Project, ProjectDao, ProjectWorkflowHelper, ProjectAssigmentHelper> {

	public ProjectTaskServiceImpl(ProcessEngine processEngine, FormHelper formHelper, BpmnHelper bpmnHelper,
			UtilContextHelper utilContextHelper, InitializationService initializationService,
			ProjectDao assetDescriptionDao, ProjectWorkflowHelper assetDescriptionHelper,
			ProjectAssigmentHelper assigmentHelper) {
		super(processEngine, formHelper, bpmnHelper, utilContextHelper, initializationService, assetDescriptionDao,
				assetDescriptionHelper, assigmentHelper);
	}

	@Override
	protected void fillProcessVariables(Map<String, Object> variables, ProjectEntity assetDescriptionEntity) {
		variables.put(ProjektWorkflowConstants.TITLE, assetDescriptionEntity.getTitle());
		if (assetDescriptionEntity.getProjectStatus() != null) {
			variables.put(ProjektWorkflowConstants.PROJECT_STATUS, assetDescriptionEntity.getStatus().name());
		}
	}

	@Override
	public String getProcessDefinitionKey() {
		return "project-process";
	}

	@Override
	@PostConstruct
	public void loadBpmn() throws IOException {
		super.loadBpmn();
	}

}
