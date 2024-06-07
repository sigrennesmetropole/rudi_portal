package org.rudi.microservice.apigateway.service.throttling.validator;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.springframework.stereotype.Component;

@Component
class FullThrottlingValidator implements ThrottlingValidator {

	@Override
	public void validateCreate(Throttling throttling) throws AppServiceException {
		validateCommon(throttling);
	}

	private void validateCommon(Throttling throttling) {
		if (throttling.getCode() == null) {
			throw new IllegalArgumentException("Code required");
		}
		if (throttling.getOrder() == null) {
			throw new IllegalArgumentException("Order id required");
		}
	}

	@Override
	public void validateUpdate(Throttling throttling) throws AppServiceException {
		if (throttling.getUuid() == null) {
			throw new IllegalArgumentException("UUID required");
		}
		validateCommon(throttling);
	}
}
