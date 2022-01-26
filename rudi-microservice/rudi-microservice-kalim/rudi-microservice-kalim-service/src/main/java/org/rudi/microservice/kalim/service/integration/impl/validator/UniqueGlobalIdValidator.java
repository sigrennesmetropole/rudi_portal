package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class UniqueGlobalIdValidator extends AbstractUniqueMetadataIdValidator<UUID> {

	private final DatasetService datasetService;

	UniqueGlobalIdValidator(GlobalIdExtractor fieldExtractor, DatasetService datasetService) {
		super(fieldExtractor);
		this.datasetService = datasetService;
	}

	@Override
	protected boolean datasetAlreadyExistsWithFieldValue(UUID globalId) throws DataverseAPIException {
		return datasetService.datasetExists(globalId);
	}
}
