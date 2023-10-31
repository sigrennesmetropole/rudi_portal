package org.rudi.facet.apimaccess.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends APIManagerHttpException {

	private static final long serialVersionUID = 7573097991425354786L;

	public UnauthorizedException() {
		super(HttpStatus.UNAUTHORIZED, null);
	}
}
