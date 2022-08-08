package org.rudi.common.service.exception;

import org.springframework.http.HttpStatus;

public class MicroserviceException extends AppServiceException {

	public MicroserviceException(String microserviceName, Throwable cause, HttpStatus statusCode, String responseBodyAsString) {
		super(String.format("Erreur reçue du microservice %s : %s : %s", microserviceName, statusCode, responseBodyAsString)
				, cause);
	}

}
