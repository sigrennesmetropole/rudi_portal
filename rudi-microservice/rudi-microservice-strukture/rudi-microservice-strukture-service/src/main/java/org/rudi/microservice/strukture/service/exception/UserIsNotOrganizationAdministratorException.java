package org.rudi.microservice.strukture.service.exception;

import org.rudi.common.service.exception.AppServiceUnauthorizedException;

public class UserIsNotOrganizationAdministratorException extends AppServiceUnauthorizedException {
	public UserIsNotOrganizationAdministratorException(String message) {
		super(message);
	}
}
