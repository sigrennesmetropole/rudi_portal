package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.microservice.kalim.service.integration.impl.handlers.IntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PostIntegrationRequestTreatmentHandler;

abstract class AbstractUniqueMetadataIdValidator<T> extends AbstractMetadataIdValidator<T> {

	AbstractUniqueMetadataIdValidator(FieldExtractor<T> fieldExtractor) {
		super(fieldExtractor);
	}

	@Override
	public boolean canBeUsedBy(IntegrationRequestTreatmentHandler handler) {
		return handler instanceof PostIntegrationRequestTreatmentHandler;
	}

	@Override
	protected boolean validationSucceedsIfDatasetAlreadyExists() {
		return false;
	}

	@Override
	protected Error304Builder getErrorBuilderForFieldValue(T fieldValue) {
		return new Error304Builder().field(getField()).fieldValue(fieldValue);
	}
}
