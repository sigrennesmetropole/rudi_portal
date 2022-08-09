package org.rudi.microservice.projekt.service.project.impl.fields;

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
	private final ProjectTypeEntityReplacer projectTypeEntityReplacer = new ProjectTypeEntityReplacer();

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}

		if (project.getProjectStatus() == ProjectStatus.VALIDATED && project.getType() == null) {
			throw new MissingParameterException("type manquant");
		}

		projectTypeEntityReplacer.replaceTransientEntitiesWithPersistentEntities(project, existingProject);
	}

	private class ProjectTypeEntityReplacer extends TransientEntitiesReplacer<ProjectTypeEntity> {

		private ProjectTypeEntityReplacer() {
			super(ProjectEntity::getType, ProjectEntity::setType);
		}

		@Nullable
		@Override
		protected ProjectTypeEntity getPersistentEntities(@Nullable ProjectTypeEntity transientProjectType) throws AppServiceException {
			if (transientProjectType == null) {
				return null;
			}

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

			return existingProjectType;
		}
	}

}
