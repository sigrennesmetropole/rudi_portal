package org.rudi.common.service.exception;

public class AppServiceBadRequestException extends AppServiceException {

	private static final long serialVersionUID = -3785599396186299538L;

	public AppServiceBadRequestException(String message) {
		super(message, AppServiceExceptionsStatus.BAD_REQUEST);
	}
}
