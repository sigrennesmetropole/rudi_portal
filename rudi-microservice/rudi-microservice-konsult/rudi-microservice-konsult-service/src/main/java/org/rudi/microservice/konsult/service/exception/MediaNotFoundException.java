package org.rudi.microservice.konsult.service.exception;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class MediaNotFoundException extends AppServiceException {

	private static final long serialVersionUID = 4043208300738973596L;

	public MediaNotFoundException(UUID mediaId, UUID metadataGlobalId) {
		super("Le média id = " + mediaId + " n'existe pas dans les métadonnées globalId = " + metadataGlobalId,
				AppServiceExceptionsStatus.NOT_FOUND);
	}
}
