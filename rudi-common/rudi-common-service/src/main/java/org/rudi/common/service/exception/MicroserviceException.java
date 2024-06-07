package org.rudi.common.service.exception;

import org.springframework.http.HttpStatus;

public class MicroserviceException extends AppServiceException {

	private static final long serialVersionUID = 5937129226068817942L;

	public MicroserviceException(String microserviceName, Throwable cause, HttpStatus statusCode,
			String responseBodyAsString) {
		super(String.format("Erreur reçue du microservice %s : %s : %s", microserviceName, statusCode,
				responseBodyAsString), cause);
	}

}
