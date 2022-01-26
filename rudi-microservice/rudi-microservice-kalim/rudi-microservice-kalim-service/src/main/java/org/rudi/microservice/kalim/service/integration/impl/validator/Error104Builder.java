package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.microservice.kalim.service.IntegrationError;

public class Error104Builder extends ErrorBuilder {
	private String fieldValue;

	@Override
	public Error104Builder field(FieldSpec fieldSpec) {
		super.field(fieldSpec);
		return this;
	}

	public <T> Error104Builder fieldValue(T fieldValue) {
		this.fieldValue = fieldValue != null ? fieldValue.toString() : null;
		return this;
	}

	@Override
	protected IntegrationError getIntegrationError() {
		return IntegrationError.ERR_104;
	}

	@Override
	protected Object[] getFormattedMessageParameters() {
		return new Object[]{
				fieldValue
		};
	}
}
