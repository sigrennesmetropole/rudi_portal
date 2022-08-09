package org.rudi.microservice.projekt.service.project.impl.fields;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.storage.dao.targetaudience.TargetAudienceDao;
import org.rudi.microservice.projekt.storage.entity.TargetAudienceEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class TargetAudienceProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final TargetAudienceDao targetAudienceDao;
	private final TargetAudienceEntityReplacer targetAudienceEntityReplacer = new TargetAudienceEntityReplacer();

	@Override
	public void process(@Nullable ProjectEntity project, @Nullable ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}
		targetAudienceEntityReplacer.replaceTransientEntitiesWithPersistentEntities(project, existingProject);
	}

	private class TargetAudienceEntityReplacer extends TransientEntitiesReplacer<Set<TargetAudienceEntity>> {
		private TargetAudienceEntityReplacer() {
			super(ProjectEntity::getTargetAudiences, ProjectEntity::setTargetAudiences);
		}

		@Nullable
		@Override
		protected Set<TargetAudienceEntity> getPersistentEntities(@Nullable Set<TargetAudienceEntity> transientEntities) throws AppServiceException {
			if (transientEntities == null) {
				return null;
			}
			final Set<TargetAudienceEntity> existingTargetAudiences = new HashSet<>();
			for (final TargetAudienceEntity targetAudience : transientEntities) {
				final var existingTargetAudience = getExistingTargetAudience(targetAudience);
				existingTargetAudiences.add(existingTargetAudience);
			}
			return existingTargetAudiences;
		}

		private TargetAudienceEntity getExistingTargetAudience(TargetAudienceEntity targetAudience)
				throws MissingParameterException, AppServiceNotFoundException {
			val uuid = targetAudience.getUuid();
			if (uuid == null) {
				throw new MissingParameterException("target_audience.uuid manquant");
			}

			try {
				return targetAudienceDao.findByUUID(uuid);
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(targetAudience, e);
			}
		}
	}

}
