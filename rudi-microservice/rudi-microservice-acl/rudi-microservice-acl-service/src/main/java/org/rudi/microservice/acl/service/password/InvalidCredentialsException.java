package org.rudi.microservice.acl.service.password;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class InvalidCredentialsException extends AbstractPasswordException {

	private static final long serialVersionUID = -142403753479750417L;

	public InvalidCredentialsException() {
		super("Mot de passe ou login incorrect", AppServiceExceptionsStatus.INVALID_CREDENTIALS);
	}
}
