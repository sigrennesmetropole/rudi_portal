package org.rudi.microservice.strukture.service.exception;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceUnprocessableEntityException;

public class CannotRemoveLastAdministratorException extends AppServiceUnprocessableEntityException {

	private static final long serialVersionUID = -6970800060269615005L;

	public CannotRemoveLastAdministratorException(UUID userUuid, UUID organizationUuid) {
		super(String.format(
				"Il n'est pas possible de retirer le dernier administrateur (userUuid = %s) de l'organisation %s",
				userUuid, organizationUuid));
	}

	public CannotRemoveLastAdministratorException(String message) {
		super(message);
	}
}
