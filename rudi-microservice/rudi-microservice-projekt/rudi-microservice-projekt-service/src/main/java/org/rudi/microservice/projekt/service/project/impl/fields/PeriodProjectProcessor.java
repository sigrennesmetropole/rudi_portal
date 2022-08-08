package org.rudi.microservice.projekt.service.project.impl.fields;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PeriodProjectProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {

	@Override
	public void process(@Nullable ProjectEntity project, @Nullable ProjectEntity existingProject) throws AppServiceException {
		if (project != null && project.getExpectedCompletionStartDate() != null &&
				project.getExpectedCompletionEndDate() != null) {
			LocalDateTime start = project.getExpectedCompletionStartDate();
			LocalDateTime end = project.getExpectedCompletionEndDate();
			if (end.isBefore(start)) {
				throw new AppServiceBadRequestException("Période de réalisation du projet saisie incohérente");
			}
		}
	}
}
