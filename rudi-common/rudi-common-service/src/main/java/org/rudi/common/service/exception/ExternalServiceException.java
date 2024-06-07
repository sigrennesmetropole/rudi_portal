package org.rudi.common.service.exception;

public class ExternalServiceException extends AppServiceException {

	private static final long serialVersionUID = 162814003857040914L;

	public ExternalServiceException(Throwable cause) {
		super("Erreur reçue du service externe", cause, AppServiceExceptionsStatus.BAD_GATEWAY);
	}

	public ExternalServiceException(String serviceName, Throwable cause) {
		super("Erreur reçue du service externe " + serviceName, cause, AppServiceExceptionsStatus.BAD_GATEWAY);
	}
}
