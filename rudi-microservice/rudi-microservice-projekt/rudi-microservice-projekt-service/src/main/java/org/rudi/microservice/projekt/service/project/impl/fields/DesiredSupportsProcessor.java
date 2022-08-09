package org.rudi.microservice.projekt.service.project.impl.fields;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.storage.dao.support.SupportDao;
import org.rudi.microservice.projekt.storage.entity.SupportEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
class DesiredSupportsProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final SupportDao supportDao;
	private final DesiredSupportsEntityReplacer desiredSupportsEntityReplacer = new DesiredSupportsEntityReplacer();

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}

		// Les types de support sont obligatoires pour les projets qui ne sont pas des réutilisations
		if (CollectionUtils.isEmpty(project.getDesiredSupports())) {
			if (project.isAReuse()) {
				return;
			} else {
				throw new MissingParameterException("desired_supports manquant");
			}
		}

		desiredSupportsEntityReplacer.replaceTransientEntitiesWithPersistentEntities(project, existingProject);
	}

	private class DesiredSupportsEntityReplacer extends TransientEntitiesReplacer<Set<SupportEntity>> {

		private DesiredSupportsEntityReplacer() {
			super(ProjectEntity::getDesiredSupports, ProjectEntity::setDesiredSupports);
		}

		@Nullable
		@Override
		protected Set<SupportEntity> getPersistentEntities(@Nullable Set<SupportEntity> desiredSupports) throws AppServiceException {
			if (desiredSupports == null) {
				return null;
			}
			final Set<SupportEntity> existingDesiredSupports = new HashSet<>(desiredSupports.size());
			for (final SupportEntity desiredSupport : desiredSupports) {
				final var existingSupport = getExistingSupport(desiredSupport);
				existingDesiredSupports.add(existingSupport);
			}
			return existingDesiredSupports;
		}

		private SupportEntity getExistingSupport(SupportEntity support)
				throws MissingParameterException, AppServiceNotFoundException {
			val uuid = support.getUuid();
			if (uuid == null) {
				throw new MissingParameterException("support.uuid manquant");
			}

			try {
				return supportDao.findByUUID(uuid);
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(support, e);
			}
		}
	}

}
