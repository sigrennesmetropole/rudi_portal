package org.rudi.microservice.projekt.service.project.impl.fields;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class ProjectStatusForDatasetModificationProcessor
		implements AddDatasetToProjectProcessor, UpdateDatasetInProjectProcessor, DeleteDatasetFromProjectProcessor {

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {

		if (existingProject != null) {

			switch (existingProject.getProjectStatus()) {
			case DRAFT:
			case REJECTED:
				// l'opération d'ajout ou de suppression de dataset est autorisée
				break;
			case VALIDATED:
				if (!existingProject.getReutilisationStatus().isDatasetSetModificationAllowed()) {
					throw new AppServiceForbiddenException(
							String.format("Cannot modify linkeddataset in project status %s",
									existingProject.getReutilisationStatus().getCode()));
				}
				break;
			case IN_PROGRESS:
			case CANCELLED:
			case DISENGAGED:
			default:
				throw new AppServiceForbiddenException(String.format("Cannot modify linkeddataset in project status %s",
						existingProject.getProjectStatus()));

			}
		}
	}

}
