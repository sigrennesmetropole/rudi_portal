package org.rudi.microservice.strukture.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class UserNotFoundException extends AppServiceException {
	public UserNotFoundException(String message, AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, exceptionStatusCode);
	}
}
