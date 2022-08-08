package org.rudi.microservice.projekt.service.project.impl.fields;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.facet.bpmn.service.TaskService;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class ProjectTaskProcessor implements DeleteProjectFieldProcessor {

	private final TaskService<Project> projectTaskService;

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (existingProject == null) {
			return;
		}
		if (projectTaskService.hasTask(existingProject.getUuid())) {
			throw new AppServiceForbiddenException(
					String.format("Project %s has a running task", existingProject.getUuid()));
		}
	}

}
