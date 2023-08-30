package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.ExternalServiceException;

public class DataverseExternalServiceException extends ExternalServiceException {

	private static final long serialVersionUID = -1654434745830001715L;

	public DataverseExternalServiceException(Throwable cause) {
		super("Dataverse", cause);
	}
}
