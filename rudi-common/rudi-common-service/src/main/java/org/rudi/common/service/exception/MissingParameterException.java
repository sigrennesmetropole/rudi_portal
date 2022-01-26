package org.rudi.common.service.exception;

public class MissingParameterException extends AppServiceException {
	public MissingParameterException(String message) {
		super(message, AppServiceExceptionsStatus.BAD_REQUEST);
	}
}
