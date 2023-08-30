package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Echec de création de compte
 * 
 * @author FNI18300
 *
 */
public abstract class AccountCreationException extends AppServiceException {

	private static final long serialVersionUID = -4445085306842794746L;

	AccountCreationException(final String message, final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, exceptionStatusCode);
	}

	AccountCreationException(final String message, final Throwable cause,
			final AppServiceExceptionsStatus exceptionStatusCode) {
		super(message, cause, exceptionStatusCode);
	}
}
