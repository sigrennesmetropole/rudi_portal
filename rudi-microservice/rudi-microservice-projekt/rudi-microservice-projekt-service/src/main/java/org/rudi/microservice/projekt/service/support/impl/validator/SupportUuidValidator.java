package org.rudi.microservice.projekt.service.support.impl.validator;

import org.rudi.microservice.projekt.core.bean.Support;
import org.springframework.stereotype.Component;

@Component
class SupportUuidValidator implements UpdateSupportValidator {
	@Override
	public void validate(Support support) throws IllegalArgumentException {
		if (support.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
	}
}
