package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceUnprocessableEntityException;

/**
 * Expriration des tocken
 * 
 * @author FNI18300
 *
 */
public class TokenExpiredException extends AppServiceUnprocessableEntityException {

	private static final long serialVersionUID = -5542477411712483709L;

	public TokenExpiredException() {
		super("Token expired");
	}
}
