package org.rudi.microservice.kalim.service.integration.impl.validator.map;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MediaValidator extends AbstractMetadataValidator<List<Media>> {
	private final ConnectorValidator connectorValidator;

	@Override
	public Set<IntegrationRequestErrorEntity> validate(List<Media> mediaList) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();

		for (Media media : mediaList) { // Tous les Connector présents dans les MEDIA du JDD
			if (media.getMediaType() == Media.MediaTypeEnum.SERVICE) { // Seuls les media SERVICE nous intéressent
				integrationRequestsErrors.addAll(connectorValidator.validate(media.getConnector()));
			}
		}

		return integrationRequestsErrors;
	}

	@Override
	protected List<Media> getMetadataElementToValidate(Metadata metadata) {
		return metadata.getAvailableFormats();
	}
}
