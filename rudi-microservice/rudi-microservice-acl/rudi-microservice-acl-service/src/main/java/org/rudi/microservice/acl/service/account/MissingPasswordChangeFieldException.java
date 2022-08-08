package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception pour un champ manquant dans l'objet {@link org.rudi.microservice.acl.core.bean.PasswordChange}
 */
public class MissingPasswordChangeFieldException extends AbstractAccountRegistrationException {
	public MissingPasswordChangeFieldException(String fieldName) {
		super("Un champ obligatoire pour modifier le mot-de-passe est absent : " + fieldName,
				AppServiceExceptionsStatus.MISSING_FIELD_PASSWORD_CHANGE);
	}
}
