package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.apache.commons.lang3.ArrayUtils;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataMetadataInfo;
import org.rudi.facet.kaccess.constant.ConstantMetadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
class MetadataInfoValidator extends AbstractMetadataValidator<MetadataMetadataInfo> {

	@Override
	public Set<IntegrationRequestErrorEntity> validate(MetadataMetadataInfo metadataMetadataInfo) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		if (metadataMetadataInfo == null) {
			String errorMessage = String.format(IntegrationError.ERR_202.getMessage(), RudiMetadataField.METADATA_INFO.getLocalName());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_202.getCode(), errorMessage, RudiMetadataField.METADATA_INFO.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		} else if (!ArrayUtils.contains(ConstantMetadata.SUPPORTED_METADATA_VERSIONS,
				metadataMetadataInfo.getApiVersion())) {
			String errorMessage = String.format(IntegrationError.ERR_106.getMessage(), metadataMetadataInfo.getApiVersion(),
					ConstantMetadata.CURRENT_METADATA_VERSION);
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_106.getCode(), errorMessage, RudiMetadataField.METADATA_INFO_API_VERSION.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		}
		return integrationRequestsErrors;
	}

	@Override
	protected MetadataMetadataInfo getMetadataElementToValidate(Metadata metadata) {
		return metadata.getMetadataInfo();
	}
}
