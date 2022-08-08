package org.rudi.microservice.projekt.service.territory.impl.validator;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.TerritorialScale;

public interface TerritorialScaleValidator {
	void validate(TerritorialScale territorialScale) throws AppServiceException;
}
