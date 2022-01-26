package org.rudi.microservice.kalim.service.helper;

import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

@Component
public class Error500Builder {
	public IntegrationRequestErrorEntity build() {
		final IntegrationError error = IntegrationError.ERR_500;
		return new IntegrationRequestErrorEntity(error.getCode(), error.getMessage());
	}
}
