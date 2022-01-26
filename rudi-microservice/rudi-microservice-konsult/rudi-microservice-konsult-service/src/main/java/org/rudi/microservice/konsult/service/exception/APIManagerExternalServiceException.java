package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.ExternalServiceException;

public class APIManagerExternalServiceException extends ExternalServiceException {
	public APIManagerExternalServiceException(Throwable cause) {
		super("API Manager", cause);
	}
}
