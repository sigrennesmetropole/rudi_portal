package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.springframework.stereotype.Component;

@Component
class UniqueDataverseDoiValidator extends AbstractUniqueMetadataIdValidator<String> {

	private final DatasetService datasetService;

	UniqueDataverseDoiValidator(DataverseDoiExtractor fieldExtractor, DatasetService datasetService) {
		super(fieldExtractor);
		this.datasetService = datasetService;
	}

	@Override
	protected boolean datasetAlreadyExistsWithFieldValue(String dataverseDoi) throws DataverseAPIException {
		return datasetService.datasetExists(dataverseDoi);
	}
}
