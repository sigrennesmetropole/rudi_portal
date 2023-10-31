package org.rudi.microservice.strukture.service.exception;

import org.rudi.common.service.exception.AppServiceUnauthorizedException;

public class UserIsNotOrganizationAdministratorException extends AppServiceUnauthorizedException {

	private static final long serialVersionUID = -6704165389184494917L;

	public UserIsNotOrganizationAdministratorException(String message) {
		super(message);
	}
}
