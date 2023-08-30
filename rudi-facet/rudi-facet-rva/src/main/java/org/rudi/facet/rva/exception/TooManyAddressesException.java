package org.rudi.facet.rva.exception;

import org.rudi.common.service.exception.BusinessException;

public class TooManyAddressesException extends BusinessException {

	private static final long serialVersionUID = 5035391878748562479L;

	public TooManyAddressesException(Throwable cause) {
		super(cause.getMessage());
	}
}
