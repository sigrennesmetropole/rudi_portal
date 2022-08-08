package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceUnprocessableEntityException;

public class TokenExpiredException extends AppServiceUnprocessableEntityException {
	public TokenExpiredException() {
		super("Token expired");
	}
}
