package org.rudi.facet.rva.exception;

import org.rudi.common.service.exception.ExternalServiceException;

public class ExternalApiRvaException extends ExternalServiceException {

	private static final long serialVersionUID = -1769108779422473925L;
	private static final String SERVICE_NAME = "api-rva";

	public ExternalApiRvaException(Throwable cause) {
		super(SERVICE_NAME, cause);
	}
}
