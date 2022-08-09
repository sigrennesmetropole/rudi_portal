package org.rudi.microservice.projekt.service.project.impl.fields;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleDao;
import org.rudi.microservice.projekt.storage.entity.TerritorialScaleEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class TerritorialScaleProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final TerritorialScaleDao territorialScaleDao;
	private final TerritorialScaleEntityReplacer territorialScaleEntityReplacer = new TerritorialScaleEntityReplacer();

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}
		territorialScaleEntityReplacer.replaceTransientEntitiesWithPersistentEntities(project, existingProject);
	}

	private class TerritorialScaleEntityReplacer extends TransientEntitiesReplacer<TerritorialScaleEntity> {
		private TerritorialScaleEntityReplacer() {
			super(ProjectEntity::getTerritorialScale, ProjectEntity::setTerritorialScale);
		}

		@Nullable
		@Override
		protected TerritorialScaleEntity getPersistentEntities(@Nullable TerritorialScaleEntity transientTerritorialScale) throws AppServiceException {
			if (transientTerritorialScale == null) {
				return null;
			}

			if (transientTerritorialScale.getUuid() == null) {
				throw new MissingParameterException("territorial_scale.uuid manquant");
			}

			final TerritorialScaleEntity existingTerritorialScale;
			try {
				existingTerritorialScale = territorialScaleDao.findByUUID(transientTerritorialScale.getUuid());
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(transientTerritorialScale, e);
			}

			return existingTerritorialScale;
		}
	}

}
