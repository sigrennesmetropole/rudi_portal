package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
class LicenceCustomValidator implements ElementValidator<LicenceCustom> {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(LicenceCustom licenceCustom) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		if (CollectionUtils.isEmpty(licenceCustom.getCustomLicenceLabel())) {
			String errorMessage = String.format(IntegrationError.ERR_202.getMessage(), RudiMetadataField.CUSTOM_LICENCE_LABEL.getLocalName());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_202.getCode(), errorMessage, RudiMetadataField.CUSTOM_LICENCE_LABEL.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		}
		return integrationRequestsErrors;
	}
}
