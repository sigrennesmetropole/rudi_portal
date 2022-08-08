package org.rudi.microservice.projekt.service.exception;

import org.rudi.common.service.exception.ExternalServiceException;

public class DataverseExternalServiceException extends ExternalServiceException {
	public DataverseExternalServiceException(Throwable cause) {
		super("Dataverse", cause);
	}
}
