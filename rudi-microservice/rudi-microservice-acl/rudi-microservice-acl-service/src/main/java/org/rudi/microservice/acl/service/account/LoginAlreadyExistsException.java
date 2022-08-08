package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception lancer lors de la détection d'une demande de création de compte pour un login déjà présent
 * 
 * @author FNI18300
 *
 */
public class LoginAlreadyExistsException extends AbstractAccountRegistrationException {

	private static final long serialVersionUID = -662058137638525242L;

	public LoginAlreadyExistsException(String login) {
		super("Le login : " + login + " est déjà associé à un utilisateur",
				AppServiceExceptionsStatus.LOGIN_ALREADY_EXISTS);
	}
}
