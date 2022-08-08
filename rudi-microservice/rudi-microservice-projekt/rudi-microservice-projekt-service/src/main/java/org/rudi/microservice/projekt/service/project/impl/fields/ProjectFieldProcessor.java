package org.rudi.microservice.projekt.service.project.impl.fields;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;

import javax.annotation.Nullable;

interface ProjectFieldProcessor {
	void process(@Nullable ProjectEntity project, @Nullable ProjectEntity existingProject) throws AppServiceException;
}
