package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

class Error303Builder extends ErrorBuilder {
	private String fieldValue;
	private String expectedString;

	@Override
	public Error303Builder field(FieldSpec fieldSpec) {
		super.field(fieldSpec);
		return this;
	}

	public Error303Builder fieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
		return this;
	}

	public Error303Builder expectedString(String expectedString) {
		this.expectedString = expectedString;
		return this;
	}

	@Override
	protected IntegrationError getIntegrationError() {
		return IntegrationError.ERR_303;
	}

	@Override
	protected Object[] getFormattedMessageParameters() {
		return new Object[]{fieldValue, fieldSpec.getLocalName(), expectedString};
	}

	@Override
	public IntegrationRequestErrorEntity build() {
		if (fieldSpec == null) {
			throw new IllegalArgumentException("Missing fieldSpec");
		}
		if (fieldValue == null) {
			throw new IllegalArgumentException("Missing fieldValue");
		}
		if (expectedString == null) {
			throw new IllegalArgumentException("Missing expectedString");
		}
		return super.build();
	}
}
