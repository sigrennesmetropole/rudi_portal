package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
class ResourceTitleValidator extends AbstractMetadataValidator<String> {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(String resourceTitle) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		if (StringUtils.isEmpty(resourceTitle)) {
			String errorMessage = String.format(IntegrationError.ERR_202.getMessage(), RudiMetadataField.RESOURCE_TITLE.getLocalName());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_202.getCode(), errorMessage, RudiMetadataField.RESOURCE_TITLE.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		}
		return integrationRequestsErrors;
	}

	@Override
	protected String getMetadataElementToValidate(Metadata metadata) {
		return metadata.getResourceTitle();
	}
}
