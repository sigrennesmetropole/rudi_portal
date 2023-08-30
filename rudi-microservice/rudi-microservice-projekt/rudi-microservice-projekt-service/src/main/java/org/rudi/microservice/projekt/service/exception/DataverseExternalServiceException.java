package org.rudi.microservice.projekt.service.exception;

import org.rudi.common.service.exception.ExternalServiceException;

public class DataverseExternalServiceException extends ExternalServiceException {

	private static final long serialVersionUID = -179405909916593993L;

	public DataverseExternalServiceException(Throwable cause) {
		super("Dataverse", cause);
	}
}
