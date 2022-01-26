package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class ExistingGlobalIdValidator extends AbstractExistingMetadataIdValidator<UUID> {

	private final DatasetService datasetService;

	ExistingGlobalIdValidator(GlobalIdExtractor fieldExtractor, DatasetService datasetService) {
		super(fieldExtractor);
		this.datasetService = datasetService;
	}

	@Override
	protected boolean datasetAlreadyExistsWithFieldValue(UUID globalId) throws DataverseAPIException {
		return datasetService.datasetExists(globalId);
	}

	@Override
	protected ErrorBuilder getErrorBuilderForFieldValue(UUID globalId) {
		return new Error104Builder()
				.field(RudiMetadataField.GLOBAL_ID)
				.fieldValue(globalId);
	}
}
