package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

import java.util.UUID;

public class MediaNotFoundException extends AppServiceException {
	public MediaNotFoundException(UUID mediaId, UUID metadataGlobalId) {
		super("Le média id = " + mediaId + " n'existe pas dans les métadonnées globalId = " + metadataGlobalId, AppServiceExceptionsStatus.NOT_FOUND);
	}
}
