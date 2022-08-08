package org.rudi.microservice.projekt.service.project.impl.fields;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.rudi.bpmn.core.bean.Status;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectStatus;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class StatusProjectProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {

	private final UtilContextHelper utilContextHelper;

	private final TaskService<Project> projectTaskService;

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (existingProject != null) {
			existingProject.setProcessDefinitionKey(projectTaskService.getProcessDefinitionKey());
		}
		// on force à DRAFT en création
		if (project != null && existingProject == null) {
			project.setProcessDefinitionKey(projectTaskService.getProcessDefinitionKey());
			project.setProjectStatus(ProjectStatus.DRAFT);
			project.setStatus(Status.DRAFT);
			if (StringUtils.isEmpty(project.getFunctionalStatus())) {
				project.setFunctionalStatus("Projet créé");
			}
			AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
			if (authenticatedUser != null) {
				project.setInitiator(authenticatedUser.getLogin());
			}
		}
	}

}
