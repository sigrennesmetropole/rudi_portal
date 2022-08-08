package org.rudi.microservice.projekt.service.project.impl.fields;

import java.util.Objects;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.storage.dao.type.ProjectTypeDao;
import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class ProjectTypeProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final ProjectTypeDao projectTypeDao;

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}

		final ProjectTypeEntity transientProjectType = project.getType();

		if (project.getProjectStatus() == ProjectStatus.VALIDATED && transientProjectType == null) {
			throw new MissingParameterException("type manquant");
		}

		if (transientProjectType != null) {

			final var uuid = transientProjectType.getUuid();
			if (uuid == null) {
				throw new MissingParameterException("type.uuid manquant");
			}

			final ProjectTypeEntity existingProjectType;
			try {
				existingProjectType = projectTypeDao.findByUUID(uuid);
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(transientProjectType, e);
			}

			// Si on est en update (existingProject != nul) alors c'est lui qu'on modifie sinon c'est l'autre (création)
			Objects.requireNonNullElse(existingProject, project).setType(existingProjectType);
		}

	}

}
