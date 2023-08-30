package org.rudi.microservice.acl.service.password;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception pour non respects des contraintes de construction du mot de passe.
 *
 * @author JGO22390
 */
public class PasswordNotMatchingRegexException extends AbstractPasswordException {

	private static final long serialVersionUID = 2659535234067500108L;

	public PasswordNotMatchingRegexException() {
		super("Le mot de passe ne correspond pas aux critères de sécurité de Rudi",
				AppServiceExceptionsStatus.PASSWORD_NOT_SECURE_ENOUGH);
	}
}
