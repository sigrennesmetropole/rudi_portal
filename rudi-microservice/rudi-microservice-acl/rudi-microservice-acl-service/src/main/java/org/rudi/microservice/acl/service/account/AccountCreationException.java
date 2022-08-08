package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public abstract class AccountCreationException extends AppServiceException {
	AccountCreationException(final String message, final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, exceptionStatusCode);
	}

	AccountCreationException(final String message, final Throwable cause,  final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, cause, exceptionStatusCode);
	}
}
