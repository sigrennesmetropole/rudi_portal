package org.rudi.microservice.projekt.service.confidentiality.impl.validator;

import org.rudi.microservice.projekt.core.bean.Confidentiality;
import org.springframework.stereotype.Component;

@Component
class ConfidentialityUuidValidator implements UpdateConfidentialityValidator {
	@Override
	public void validate(Confidentiality confidentiality) throws IllegalArgumentException {
		if (confidentiality.getUuid() == null) {
			throw new IllegalArgumentException("UUID manquant");
		}
	}
}
