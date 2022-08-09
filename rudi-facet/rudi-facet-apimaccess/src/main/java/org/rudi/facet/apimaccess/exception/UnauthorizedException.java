package org.rudi.facet.apimaccess.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends APIManagerHttpException {
	public UnauthorizedException() {
		super(HttpStatus.UNAUTHORIZED, null);
	}
}
