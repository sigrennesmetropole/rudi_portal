package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception le login n'est pas une adresse mail (création de compte utilisateur)
 * 
 * @author FNI18300
 *
 */
public class LoginNotMailException extends AbstractAccountRegistrationException {

	private static final long serialVersionUID = -8297208483093710974L;

	public LoginNotMailException(String login) {
		super("Le login : " + login + " n'est pas une adresse mail", AppServiceExceptionsStatus.LOGIN_NOT_MAIL);
	}
}
