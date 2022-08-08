package org.rudi.microservice.acl.service.account;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception pour un champ manquant
 * 
 * @author FNI18300
 *
 */
public class MissingFieldException extends AbstractAccountRegistrationException {
	public MissingFieldException(String fieldName) {
		super("Un champ obligatoire pour créer le compte est absent : " + fieldName,
				AppServiceExceptionsStatus.MISSING_FIELD_ACCOUNT);
	}
}
