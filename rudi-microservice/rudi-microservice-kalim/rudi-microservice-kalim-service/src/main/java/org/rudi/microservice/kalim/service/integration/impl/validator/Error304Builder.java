package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.microservice.kalim.service.IntegrationError;

public class Error304Builder extends ErrorBuilder {
	private String fieldValue;

	@Override
	public Error304Builder field(FieldSpec fieldSpec) {
		super.field(fieldSpec);
		return this;
	}

	public <T> Error304Builder fieldValue(T fieldValue) {
		this.fieldValue = fieldValue != null ? fieldValue.toString() : null;
		return this;
	}

	@Override
	protected IntegrationError getIntegrationError() {
		return IntegrationError.ERR_304;
	}

	@Override
	protected Object[] getFormattedMessageParameters() {
		return new Object[]{
				fieldValue,
				fieldSpec.getLocalName()
		};
	}
}
