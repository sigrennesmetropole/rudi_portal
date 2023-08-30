package org.rudi.microservice.strukture.service.exception;

import org.rudi.common.service.exception.BusinessException;

public class UserIsAlreadyOrganizationMemberException extends BusinessException {
	/**
	 * Construction de l'objet, le message d'erreur n'est exploité que côté back pour les logs
	 *
	 * @param message message d'explication côté back
	 */
	public UserIsAlreadyOrganizationMemberException(String message) {
		super(message);
	}
}
