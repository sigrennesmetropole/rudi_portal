package org.rudi.facet.apimaccess.exception;

import org.springframework.http.HttpStatus;

public class APIEndpointException extends APIManagerException {
	public APIEndpointException(HttpStatus httpStatus) {
		super("HTTP " + httpStatus + " reçu du endpoint de l'API.");
	}
}
