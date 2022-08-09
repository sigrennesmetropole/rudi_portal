package org.rudi.microservice.projekt.service.project.impl.fields;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.service.confidentiality.impl.ConfidentialityHelper;
import org.rudi.microservice.projekt.storage.dao.confidentiality.ConfidentialityDao;
import org.rudi.microservice.projekt.storage.entity.ConfidentialityEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class ConfidentialityProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final ConfidentialityDao confidentialityDao;
	private final ConfidentialityHelper confidentialityHelper;
	private final ConfidentialityEntityReplacer confidentialityEntityReplacer = new ConfidentialityEntityReplacer();

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}
		confidentialityEntityReplacer.replaceTransientEntitiesWithPersistentEntities(project, existingProject);
	}

	private class ConfidentialityEntityReplacer extends TransientEntitiesReplacer<ConfidentialityEntity> {

		private ConfidentialityEntityReplacer() {
			super(ProjectEntity::getConfidentiality, ProjectEntity::setConfidentiality);
		}

		@Nullable
		@Override
		protected ConfidentialityEntity getPersistentEntities(@Nullable ConfidentialityEntity transientConfidentiality) throws AppServiceException {
			final ConfidentialityEntity existingConfidentiality;
			if (transientConfidentiality == null) {
				existingConfidentiality = confidentialityHelper.getDefaultConfidentiality();
			} else if (transientConfidentiality.getUuid() == null) {
				throw new MissingParameterException("confidentiality.uuid manquant");
			} else {
				try {
					existingConfidentiality = confidentialityDao.findByUUID(transientConfidentiality.getUuid());
				} catch (EmptyResultDataAccessException e) {
					throw new AppServiceNotFoundException(transientConfidentiality, e);
				}
			}
			return existingConfidentiality;
		}
	}

}
