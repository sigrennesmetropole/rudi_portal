package org.rudi.microservice.projekt.service.project.impl.fields;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.projekt.storage.dao.support.SupportDao;
import org.rudi.microservice.projekt.storage.entity.SupportEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectStatus;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
class DesiredSupportsProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {
	private final SupportDao supportDao;

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}

		final Set<SupportEntity> desiredSupports = project.getDesiredSupports();
		if (project.getProjectStatus() == ProjectStatus.DRAFT && CollectionUtils.isEmpty(desiredSupports)) {
			throw new MissingParameterException("desired_supports manquant");
		} else if (CollectionUtils.isEmpty(desiredSupports)) {
			return;
		}

		final Set<SupportEntity> existingDesiredSupports = new HashSet<>(desiredSupports.size());
		for (final SupportEntity desiredSupport : desiredSupports) {
			final var existingSupport = getExistingSupport(desiredSupport);
			existingDesiredSupports.add(existingSupport);
		}

		// Si on est en update (existingProject != nul) alors c'est lui qu'on modifie sinon c'est l'autre (création)
		Objects.requireNonNullElse(existingProject, project).setDesiredSupports(existingDesiredSupports);
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
