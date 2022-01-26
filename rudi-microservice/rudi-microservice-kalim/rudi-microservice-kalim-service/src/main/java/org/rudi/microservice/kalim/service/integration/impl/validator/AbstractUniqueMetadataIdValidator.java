package org.rudi.microservice.kalim.service.integration.impl.validator;

import lombok.extern.slf4j.Slf4j;
import org.rudi.microservice.kalim.service.integration.impl.handlers.IntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PostIntegrationRequestTreatmentHandler;

@Slf4j
abstract class AbstractUniqueMetadataIdValidator<T> extends AbstractMetadataIdValidator<T> {

	public AbstractUniqueMetadataIdValidator(FieldExtractor<T> fieldExtractor) {
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
		return new Error304Builder()
				.field(getField())
				.fieldValue(fieldValue);
	}
}
