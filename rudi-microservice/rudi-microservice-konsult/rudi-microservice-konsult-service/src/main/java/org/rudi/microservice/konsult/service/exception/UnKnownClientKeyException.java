package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class UnKnownClientKeyException extends AppServiceException {

    public UnKnownClientKeyException() {
        super("Les clés clientKey de l'utilisateur sont inconnues", AppServiceExceptionsStatus.UNKNOWN_CLIENT_KEY);
    }
}
