package org.rudi.microservice.konsult.service.exception;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class AccessDeniedMetadataMediaException extends AppServiceException {

    public AccessDeniedMetadataMediaException(UUID mediaId, UUID globalId) {
        super(String.format("L'utilisateur connecté ne peut pas accéder au média media_id = %s du jeu de données global_id = %s", mediaId, globalId),
                AppServiceExceptionsStatus.ACCESS_DENIED_METADATA_MEDIA);
    }
}
