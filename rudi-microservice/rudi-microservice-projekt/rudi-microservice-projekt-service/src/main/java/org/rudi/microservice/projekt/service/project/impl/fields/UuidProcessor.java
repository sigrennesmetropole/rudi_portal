package org.rudi.microservice.projekt.service.project.impl.fields;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

@Component
class UuidProcessor implements UpdateProjectFieldProcessor {
	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project != null && project.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
	}

}
