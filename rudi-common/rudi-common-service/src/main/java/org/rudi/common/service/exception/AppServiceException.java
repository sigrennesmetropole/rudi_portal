package org.rudi.common.service.exception;

import lombok.Getter;

public class AppServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	@Getter
	private final AppServiceExceptionsStatus appExceptionStatusCode;


	public AppServiceException() {
		this(null, null, null);
	}

	public AppServiceException(final String message) {
		this(message, null, null);
	}

	public AppServiceException(final String message, final AppServiceExceptionsStatus exceptionStatusCode) {
		this(message, null, exceptionStatusCode);
	}

	public AppServiceException(final String message, final Throwable cause) {
		this(message, cause, null);
	}

	public AppServiceException(final String message, final Throwable cause, final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, cause);
		this.appExceptionStatusCode = exceptionStatusCode;
	}

}
