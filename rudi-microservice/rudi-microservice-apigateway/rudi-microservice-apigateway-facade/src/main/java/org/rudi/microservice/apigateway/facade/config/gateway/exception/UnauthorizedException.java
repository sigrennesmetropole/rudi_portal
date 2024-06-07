package org.rudi.microservice.apigateway.facade.config.gateway.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends APIManagerHttpException {

	private static final long serialVersionUID = 7573097991425354786L;

	public UnauthorizedException() {
		super(HttpStatus.UNAUTHORIZED, null);
	}

	public UnauthorizedException(String message) {
		super(HttpStatus.UNAUTHORIZED, message);
	}
}
