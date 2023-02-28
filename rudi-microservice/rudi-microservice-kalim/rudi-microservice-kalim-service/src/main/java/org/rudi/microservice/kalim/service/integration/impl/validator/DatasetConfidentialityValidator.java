package org.rudi.microservice.kalim.service.integration.impl.validator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

@Component
class DatasetConfidentialityValidator extends AbstractMetadataValidator<MetadataAccessConditionConfidentiality> {

	@Override
	protected MetadataAccessConditionConfidentiality getMetadataElementToValidate(Metadata metadata) {
		return metadata.getAccessCondition().getConfidentiality();
	}
	/**
	 * Validation de l'attribut confidentiality
	 *
	 * @param metadataAccessConditionConfidentiality   MetadataAccessConditionConfidentiality
	 * @return  						liste des erreurs
	 */
	@Override
	public Set<IntegrationRequestErrorEntity> validate(MetadataAccessConditionConfidentiality metadataAccessConditionConfidentiality) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		if (metadataAccessConditionConfidentiality != null) {
			if(Boolean.TRUE.equals(metadataAccessConditionConfidentiality.getGdprSensitive()) && Boolean.FALSE.equals(metadataAccessConditionConfidentiality.getRestrictedAccess())){
				String errorMessage = String.format(IntegrationError.ERR_306.getMessage(), RudiMetadataField.GDPR_SENSITIVE.getLocalName(),RudiMetadataField.RESTRICTED_ACCESS.getLocalName());
				IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
						UUID.randomUUID(), IntegrationError.ERR_306.getCode(), errorMessage, RudiMetadataField.GDPR_SENSITIVE.getLocalName()+RudiMetadataField.RESTRICTED_ACCESS.getLocalName(), LocalDateTime.now());

				integrationRequestsErrors.add(integrationRequestError);
			}

		} else {
			String errorMessage = String.format(IntegrationError.ERR_202.getMessage(), RudiMetadataField.CONFIDENTIALITY.getLocalName());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_202.getCode(), errorMessage, RudiMetadataField.CONFIDENTIALITY.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		}
		return integrationRequestsErrors;
	}
}
