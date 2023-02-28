package org.rudi.microservice.selfdata.service.exception;

import org.rudi.common.service.exception.AppServiceException;

public class TechnicalWso2CallException extends AppServiceException {

	public TechnicalWso2CallException(Throwable throwable) {
		super("Erreur technique lors de l'appel vers une API WSO2 selfdata (TPBC ou GDATA)", throwable);
	}
}
