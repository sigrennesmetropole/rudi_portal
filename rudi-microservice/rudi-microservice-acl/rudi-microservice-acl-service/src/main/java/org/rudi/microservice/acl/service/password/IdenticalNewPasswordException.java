package org.rudi.microservice.acl.service.password;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class IdenticalNewPasswordException extends AbstractPasswordException {

	public IdenticalNewPasswordException() {
		super("Le nouveau mot de passe ne peut pas être identique à l'ancien mot de passe",
				AppServiceExceptionsStatus.IDENTICAL_NEW_PASSWORD);
	}
}
