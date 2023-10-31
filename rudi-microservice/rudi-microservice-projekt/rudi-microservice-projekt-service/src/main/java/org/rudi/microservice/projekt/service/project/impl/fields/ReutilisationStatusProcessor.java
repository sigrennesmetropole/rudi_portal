package org.rudi.microservice.projekt.service.project.impl.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusDao;
import org.rudi.microservice.projekt.storage.entity.ReutilisationStatusEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReutilisationStatusProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor{
	private final ReutilisationStatusDao reutilisationStatusDao;
	private final ReutilisationStatusEntityReplacer reutilisationStatusEntityReplacer = new ReutilisationStatusEntityReplacer();

	@Override
	public void process(@Nullable ProjectEntity project, @Nullable ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}
		reutilisationStatusEntityReplacer.replaceTransientEntitiesWithPersistentEntities(project, existingProject);
	}

	private class ReutilisationStatusEntityReplacer extends TransientEntitiesReplacer<ReutilisationStatusEntity> {

		private ReutilisationStatusEntityReplacer(){
			super(ProjectEntity::getReutilisationStatus, ProjectEntity::setReutilisationStatus);
		}

		@Nullable
		@Override
		protected ReutilisationStatusEntity getPersistentEntities(@Nullable ReutilisationStatusEntity transientReutilisationStatus) throws AppServiceException {
			final ReutilisationStatusEntity existingReutilisationStatus;

			if (transientReutilisationStatus == null) { // Il n'y a pas de statut par défaut, il en faut donc un dans le projet en création
				throw  new MissingParameterException("Un statut de réutilisation est obligatoire");
			} else if (transientReutilisationStatus.getUuid() == null) {
				throw new MissingParameterException("reutilisation_status.uuid manquant");
			} else {
				try {
					existingReutilisationStatus = reutilisationStatusDao.findByUUID(transientReutilisationStatus.getUuid());
				} catch (EmptyResultDataAccessException e) {
					throw new AppServiceNotFoundException(transientReutilisationStatus, e);
				}
			}
			return existingReutilisationStatus;
		}
	}
}
