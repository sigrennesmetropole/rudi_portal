package org.rudi.microservice.projekt.service.project.impl.fields;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleDao;
import org.rudi.microservice.projekt.storage.entity.TerritorialScaleEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Objects;

@Component
@RequiredArgsConstructor
class TerritorialScaleProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final TerritorialScaleDao territorialScaleDao;

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}

		final TerritorialScaleEntity transientTerritorialScale = project.getTerritorialScale();
		if (transientTerritorialScale != null) {
			if (transientTerritorialScale.getUuid() == null) {
				throw new MissingParameterException("territorial_scale.uuid manquant");
			}

			final TerritorialScaleEntity existingTerritorialScale;
			try {
				existingTerritorialScale = territorialScaleDao.findByUUID(transientTerritorialScale.getUuid());
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(transientTerritorialScale, e);
			}

			// Si on est en update (existingProject != nul) alors c'est lui qu'on modifie sinon c'est l'autre (création)
			Objects.requireNonNullElse(existingProject, project).setTerritorialScale(existingTerritorialScale);
		}
	}

}
