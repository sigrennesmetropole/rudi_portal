package org.rudi.microservice.konsent.service.exception;

import org.rudi.common.service.exception.BusinessException;

public class KonsentUnauthorizedException extends BusinessException {

	private static final long serialVersionUID = -3326923315942715352L;

	/**
	 * Construction de l'objet, le message d'erreur n'est exploité que côté back pour les logs
	 *
	 * @param message message d'explication côté back
	 */
	public KonsentUnauthorizedException(String message) {
		super(message);
	}
}
