package org.rudi.common.service.exception;

import org.apache.commons.lang3.StringUtils;

/**
 * Représente une erreur métier à expliquer à l'utilisateur final côté front
 */
public abstract class BusinessException extends AppServiceException {

	/**
	 * Construction de l'objet, le message d'erreur n'est exploité que côté back pour les logs
	 *
	 * @param message message d'explication côté back
	 */
	protected BusinessException(String message) {
		super(message, AppServiceExceptionsStatus.BUSINESS_ERROR);
	}

	/**
	 * Construction de la clé de translate à partir du nom de la classe
	 * @return un nom de clé de translate
	 */
	public String getTranslateKey() {
		return StringUtils.uncapitalize(getClass().getSimpleName());
	}
}
