package org.rudi.common.service.exception;

public class AppServiceBadRequestException extends AppServiceException {
	public AppServiceBadRequestException(String message) {
		super(message, AppServiceExceptionsStatus.BAD_REQUEST);
	}
}
