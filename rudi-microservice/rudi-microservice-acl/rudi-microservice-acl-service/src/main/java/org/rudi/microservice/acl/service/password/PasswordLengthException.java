package org.rudi.microservice.acl.service.password;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception pour non respect des contraintes sur le mot de passe
 * 
 * @author FNI18300
 *
 */
public class PasswordLengthException extends AbstractPasswordException {

	private static final long serialVersionUID = 9046218198421283510L;

	public PasswordLengthException(int minLength, int maxLength) {
		super("Le mot de passe doit avoir une taille comprise entre " + minLength + " et " + maxLength + " caractères",
				AppServiceExceptionsStatus.PASSWORD_LENGTH);
	}
}
