package org.rudi.common.service.exception;

public class AppServiceForbiddenException extends AppServiceException {
	public AppServiceForbiddenException(String message) {
		super(message, AppServiceExceptionsStatus.FORBIDDEN);
	}
}
