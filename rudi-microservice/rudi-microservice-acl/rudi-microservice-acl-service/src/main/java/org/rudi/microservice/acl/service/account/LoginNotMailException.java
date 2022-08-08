package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class LoginNotMailException extends AbstractAccountRegistrationException {
	public LoginNotMailException(String login) {
		super("Le login : " + login + " n'est pas une adresse mail", AppServiceExceptionsStatus.LOGIN_NOT_MAIL);
	}
}
