package org.rudi.microservice.acl.service.password;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class InvalidCredentialsException extends AbstractPasswordException {

	public InvalidCredentialsException() {
		super("Mot de passe ou login incorrect", AppServiceExceptionsStatus.INVALID_CREDENTIALS);
	}
}
