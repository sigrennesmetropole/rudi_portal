package org.rudi.microservice.strukture.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class UserNotFoundException extends AppServiceException {

	private static final long serialVersionUID = -9180049601771486338L;

	public UserNotFoundException(String message, AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, exceptionStatusCode);
	}
}
