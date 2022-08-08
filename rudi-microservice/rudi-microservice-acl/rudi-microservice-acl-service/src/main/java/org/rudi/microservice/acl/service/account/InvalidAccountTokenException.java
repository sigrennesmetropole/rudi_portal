/**
 * RUDI Portail
 */
package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * @author FNI18300
 *
 */
public class InvalidAccountTokenException extends AbstractAccountRegistrationException {

	private static final long serialVersionUID = -1169909903457939189L;

	public InvalidAccountTokenException() {
		super("Le token d'activation du compte est inconnu ou invalide", AppServiceExceptionsStatus.BAD_REQUEST);
	}
}
