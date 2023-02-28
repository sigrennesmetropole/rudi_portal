package org.rudi.microservice.selfdata.service.exception;

import org.rudi.common.service.exception.AppServiceException;

public class UserNotFoundException extends AppServiceException {
	private static final long serialVersionUID = 5445171823744544621L;

	public UserNotFoundException(String message) {
		super(message);
	}
}
