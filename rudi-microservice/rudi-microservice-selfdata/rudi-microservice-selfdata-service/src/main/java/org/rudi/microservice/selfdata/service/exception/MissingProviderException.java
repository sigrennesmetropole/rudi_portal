package org.rudi.microservice.selfdata.service.exception;

import org.rudi.common.service.exception.AppServiceException;

public class MissingProviderException extends AppServiceException {
	private static final long serialVersionUID = 4834547874599555649L;

	public MissingProviderException(String message) {
		super(message);
	}
}