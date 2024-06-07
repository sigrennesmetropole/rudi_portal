package org.rudi.microservice.apigateway.facade.config.gateway.exception;

import javax.annotation.Nullable;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class APIManagerHttpException extends APIManagerException {

	private static final long serialVersionUID = -7444998750548812238L;

	private final HttpStatus statusCode;

	public APIManagerHttpException(HttpStatus statusCode, @Nullable String errorBody) {
		super(getMessage(statusCode, errorBody));
		this.statusCode = statusCode;
	}

	private static String getMessage(HttpStatus statusCode, @Nullable String errorBody) {
		return String.format("HTTP %s received from API Manager %s", statusCode,
				errorBody != null ? String.format("with body : %s", errorBody) : "without body");
	}

}
