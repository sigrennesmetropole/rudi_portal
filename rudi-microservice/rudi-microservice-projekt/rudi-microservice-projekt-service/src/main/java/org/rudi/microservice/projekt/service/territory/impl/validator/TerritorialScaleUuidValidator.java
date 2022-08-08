package org.rudi.microservice.projekt.service.territory.impl.validator;

import org.rudi.microservice.projekt.core.bean.TerritorialScale;
import org.springframework.stereotype.Component;

@Component
class TerritorialScaleUuidValidator implements UpdateTerritorialScaleValidator {
	@Override
	public void validate(TerritorialScale territorialScale) throws IllegalArgumentException {
		if (territorialScale.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
	}
}
