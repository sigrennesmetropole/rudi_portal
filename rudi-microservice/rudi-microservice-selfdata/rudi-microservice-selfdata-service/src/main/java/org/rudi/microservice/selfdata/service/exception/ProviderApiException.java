package org.rudi.microservice.selfdata.service.exception;

import org.rudi.common.service.exception.AppServiceException;

public class ProviderApiException extends AppServiceException {
	private static final long serialVersionUID = -5528069557589242233L;

	public ProviderApiException(String message, Throwable cause) {
		super(message, cause);
	}
}
