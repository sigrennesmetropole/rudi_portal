package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * classe abstaite pour les exceptions d'enregistrement des comptes
 * 
 * @author FNI18300
 *
 */
public abstract class AbstractAccountRegistrationException extends AppServiceException {

	private static final long serialVersionUID = 5743767094171764770L;

	AbstractAccountRegistrationException(final String message, final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, exceptionStatusCode);
	}
}
