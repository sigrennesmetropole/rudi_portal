package org.rudi.common.service.exception;

public class ExternalServiceException extends AppServiceException {
	public ExternalServiceException(String serviceName, Throwable cause) {
		super("Erreur reçue du service externe " + serviceName, cause, AppServiceExceptionsStatus.BAD_GATEWAY);
	}
}
