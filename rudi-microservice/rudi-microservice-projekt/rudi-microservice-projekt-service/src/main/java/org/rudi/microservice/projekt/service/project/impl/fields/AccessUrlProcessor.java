package org.rudi.microservice.projekt.service.project.impl.fields;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
class AccessUrlProcessor implements CreateProjectFieldProcessor, UpdateProjectFieldProcessor {

	private static final List<String> ACCEPTED_SCHEMES = Arrays.asList("http", "https", "ftp");

	@Override
	public void process(@Nullable ProjectEntity project, ProjectEntity existingProject) throws AppServiceException {
		if (project == null) {
			return;
		}

		final var accessUrl = project.getAccessUrl();
		if (StringUtils.isNotBlank(accessUrl)) {
			final var scheme = getScheme(accessUrl);
			if (scheme == null) {
				throw new AppServiceBadRequestException("Schéma manquant pour le champ access_url (par exemple \"https://\")");
			}

			if (!ACCEPTED_SCHEMES.contains(scheme)) {
				throw new AppServiceBadRequestException(String.format(
						"Schéma %s non accepté pour le champ access_url. Schémas acceptés : %s",
						scheme,
						StringUtils.join(ACCEPTED_SCHEMES, ", ")
				));
			}
		}
	}

	@Nullable
	private String getScheme(String accessUrl) {
		try {
			return new URI(accessUrl).getScheme();
		} catch (URISyntaxException e) {
			log.error("Cannot get scheme from access_url " + accessUrl);
			return null;
		}
	}

}
