package org.rudi.common.service.exception;

public class MissingParameterException extends AppServiceBadRequestException {
	public MissingParameterException(String message) {
		super(message);
	}
}
