package org.rudi.microservice.strukture.service.exception;

import org.rudi.common.service.exception.AppServiceUnprocessableEntityException;

import java.util.UUID;

public class CannotRemoveLastAdministratorException extends AppServiceUnprocessableEntityException {
	public CannotRemoveLastAdministratorException(UUID userUuid, UUID organizationUuid) {
		super(String.format("Il n'est pas possible de retirer le dernier administrateur (userUuid = %s) de l'organisation %s", userUuid, organizationUuid));
	}
}
