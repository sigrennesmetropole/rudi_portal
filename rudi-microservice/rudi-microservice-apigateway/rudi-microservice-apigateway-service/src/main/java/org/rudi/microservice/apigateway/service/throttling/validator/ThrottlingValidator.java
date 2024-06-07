package org.rudi.microservice.apigateway.service.throttling.validator;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Throttling;

public interface ThrottlingValidator {
	void validateCreate(Throttling throttling) throws AppServiceException;

	void validateUpdate(Throttling throttling) throws AppServiceException;
}
