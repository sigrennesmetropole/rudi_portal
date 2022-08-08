package org.rudi.microservice.konsult.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;

public class ClientKeyNotFoundException extends AppServiceException {

    public ClientKeyNotFoundException(String username) {
        super(String.format("La clé client de l'utilisateur %s n'a pas été trouvée dans ACL",
                        username),
                AppServiceExceptionsStatus.UNKNOWN_CLIENT_KEY);
    }
}
