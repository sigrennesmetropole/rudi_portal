package org.rudi.common.service.exception;

public class AppServiceUnauthorizedException extends AppServiceException {

	private static final long serialVersionUID = -3188149015287991129L;

	public AppServiceUnauthorizedException(String message) {
		super(message, AppServiceExceptionsStatus.UNAUTHORIZE);
	}

	public AppServiceUnauthorizedException(String message, Throwable t) {
		super(message, t, AppServiceExceptionsStatus.UNAUTHORIZE);
	}
}
