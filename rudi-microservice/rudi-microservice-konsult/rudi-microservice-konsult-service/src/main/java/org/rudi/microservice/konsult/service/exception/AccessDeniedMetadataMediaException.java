package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

import java.util.UUID;

public class AccessDeniedMetadataMediaException extends AppServiceException {

    public AccessDeniedMetadataMediaException(String login, UUID globalId, UUID mediaId) {
        super(String.format("L'utilisateur %s ne peut pas accéder au média media_id = %s du jeu de données global_id = %s", login, globalId, mediaId),
                AppServiceExceptionsStatus.ACCESS_DENIED_METADATA_MEDIA);
    }
}
