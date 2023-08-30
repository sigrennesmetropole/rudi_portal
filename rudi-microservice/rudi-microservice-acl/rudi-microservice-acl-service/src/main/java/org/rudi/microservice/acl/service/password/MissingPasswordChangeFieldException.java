package org.rudi.microservice.acl.service.password;

import org.rudi.common.service.exception.AppServiceExceptionsStatus;

/**
 * Exception pour un champ manquant dans l'objet {@link org.rudi.microservice.acl.core.bean.PasswordChange}
 */
public class MissingPasswordChangeFieldException extends AbstractPasswordException {

	private static final long serialVersionUID = -170165373055562551L;

	public MissingPasswordChangeFieldException(String fieldName) {
		super("Un champ obligatoire pour modifier le mot-de-passe est absent : " + fieldName,
				AppServiceExceptionsStatus.MISSING_FIELD_PASSWORD_CHANGE);
	}
}
