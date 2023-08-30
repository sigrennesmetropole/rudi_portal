package org.rudi.microservice.acl.service.password;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public abstract class AbstractPasswordException extends AppServiceException {

	public AbstractPasswordException(String message, AppServiceExceptionsStatus status) {
		super(message, status);
	}
}
