package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

import java.util.UUID;

public class AccessDeniedMetadataMedia extends AppServiceException {

    public AccessDeniedMetadataMedia(UUID globalId, UUID mediaId) {
        super(String.format("L'utilisateur ne peut pas accéder au média media_id = %s du jeu de données global_id = %s", globalId, mediaId),
                AppServiceExceptionsStatus.ACCESS_DENIED_METADATA_MEDIA);
    }
}
