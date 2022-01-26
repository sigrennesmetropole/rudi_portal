package org.rudi.microservice.kalim.service.integration.impl.validator;

import lombok.extern.slf4j.Slf4j;
import org.rudi.microservice.kalim.service.integration.impl.handlers.IntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PutIntegrationRequestTreatmentHandler;

@Slf4j
abstract class AbstractExistingMetadataIdValidator<T> extends AbstractMetadataIdValidator<T> {

	public AbstractExistingMetadataIdValidator(FieldExtractor<T> fieldExtractor) {
		super(fieldExtractor);
	}

	@Override
	public boolean canBeUsedBy(IntegrationRequestTreatmentHandler handler) {
		return handler instanceof PutIntegrationRequestTreatmentHandler;
	}

	@Override
	protected boolean validationSucceedsIfDatasetAlreadyExists() {
		return true;
	}
}
