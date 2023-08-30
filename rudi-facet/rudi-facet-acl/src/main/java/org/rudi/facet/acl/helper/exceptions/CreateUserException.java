package org.rudi.facet.acl.helper.exceptions;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class CreateUserException extends AppServiceException {

	private static final long serialVersionUID = -2710526509324525052L;

	public CreateUserException(final String message, final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, exceptionStatusCode);
	}

	public CreateUserException(final String message, final Throwable cause,
			final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, cause, exceptionStatusCode);
	}
}