package org.rudi.common.service.exception;

public class AppServiceForbiddenException extends AppServiceException {

	private static final long serialVersionUID = 3470943277235180158L;

	public AppServiceForbiddenException(String message) {
		super(message, AppServiceExceptionsStatus.FORBIDDEN);
	}
}
