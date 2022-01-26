package org.rudi.microservice.kalim.service.integration.impl.validator;

import lombok.RequiredArgsConstructor;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class AccessConditionValidator extends AbstractMetadataValidator<MetadataAccessCondition> {

	private final LicenceValidator licenceValidator;

	@Override
	protected MetadataAccessCondition getMetadataElementToValidate(Metadata metadata) {
		return metadata.getAccessCondition();
	}

	/**
	 * Validation de l'attribut access_condition
	 *
	 * @param metadataAccessCondition   MetadataAccessCondition
	 * @return  						liste des erreurs
	 */
	@Override
	public Set<IntegrationRequestErrorEntity> validate(MetadataAccessCondition metadataAccessCondition) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		if (metadataAccessCondition != null) {
			integrationRequestsErrors.addAll(licenceValidator.validate(metadataAccessCondition.getLicence()));
		} else {
			String errorMessage = String.format(IntegrationError.ERR_202.getMessage(), RudiMetadataField.ACCESS_CONDITION.getLocalName());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_202.getCode(), errorMessage, RudiMetadataField.ACCESS_CONDITION.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		}
		return integrationRequestsErrors;
	}
}
