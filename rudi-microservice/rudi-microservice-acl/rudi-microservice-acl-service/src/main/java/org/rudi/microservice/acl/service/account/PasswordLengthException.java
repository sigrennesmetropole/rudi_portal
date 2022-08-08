package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception pour non respect des contraintes sur le mot de passe
 * 
 * @author FNI18300
 *
 */
public class PasswordLengthException extends AbstractAccountRegistrationException {
	public PasswordLengthException(int minLength, int maxLength) {
		super("Le mot de passe doit avoir une taille comprise entre " + minLength + " et " + maxLength + " caractères",
				AppServiceExceptionsStatus.PASSWORD_LENGTH);
	}
}
