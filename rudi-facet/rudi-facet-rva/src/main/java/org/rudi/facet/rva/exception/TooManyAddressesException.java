package org.rudi.facet.rva.exception;


import org.rudi.common.service.exception.BusinessException;

public class TooManyAddressesException extends BusinessException {
	public TooManyAddressesException(Throwable cause) {
		super(cause.getMessage());
	}
}
