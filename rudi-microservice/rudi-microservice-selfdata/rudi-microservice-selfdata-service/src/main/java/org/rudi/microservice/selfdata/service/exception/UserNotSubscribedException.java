package org.rudi.microservice.selfdata.service.exception;

import org.rudi.common.service.exception.BusinessException;

public class UserNotSubscribedException extends BusinessException {

	public UserNotSubscribedException() {
		super("L'utilisateur connecté n'a pas activé les accès par API ou n'a pas souscrit aux APIs");
	}
}
