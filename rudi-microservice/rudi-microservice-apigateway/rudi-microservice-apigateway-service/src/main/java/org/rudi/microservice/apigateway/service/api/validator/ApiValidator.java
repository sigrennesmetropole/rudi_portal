package org.rudi.microservice.apigateway.service.api.validator;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Api;

public interface ApiValidator {
	void validateCreate(Api api) throws AppServiceException;

	void validateUpdate(Api api) throws AppServiceException;
}
