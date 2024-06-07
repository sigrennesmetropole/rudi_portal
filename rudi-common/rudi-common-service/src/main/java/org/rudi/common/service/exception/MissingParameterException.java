package org.rudi.common.service.exception;

public class MissingParameterException extends AppServiceBadRequestException {

	private static final long serialVersionUID = 5526327090436455810L;

	public MissingParameterException(String message) {
		super(message);
	}
}
