package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.ExternalServiceException;

public class APIManagerExternalServiceException extends ExternalServiceException {

	private static final long serialVersionUID = 8766688589378071282L;

	public APIManagerExternalServiceException(Throwable cause) {
		super("API Manager", cause);
	}
}
