package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.time.LocalDateTime;
import java.util.UUID;

abstract class ErrorBuilder {
	protected FieldSpec fieldSpec;

	public ErrorBuilder field(FieldSpec fieldSpec) {
		this.fieldSpec = fieldSpec;
		return this;
	}

	public IntegrationRequestErrorEntity build() {
		final IntegrationError error = getIntegrationError();
		final String errorMessage = String.format(error.getMessage(), getFormattedMessageParameters());
		return new IntegrationRequestErrorEntity(
				UUID.randomUUID(), error.getCode(), errorMessage, fieldSpec.getLocalName(), LocalDateTime.now());
	}

	protected abstract IntegrationError getIntegrationError();

	protected Object[] getFormattedMessageParameters() {
		return new Object[0];
	}
}
