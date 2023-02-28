package org.rudi.microservice.kalim.service.integration.impl.validator;

import java.util.HashSet;
import java.util.Set;

import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.SelfdataContent;
import org.rudi.facet.kaccess.helper.selfdata.SelfdataMediaHelper;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class GdprSensitiveMetadataExtMetadataValidator extends AbstractMetadataValidator<Metadata> {

	private final SelfdataMediaHelper selfdataMediaHelper;

	@Override
	protected Metadata getMetadataElementToValidate(Metadata metadata) {
		return metadata;
	}


	@Override
	public Set<IntegrationRequestErrorEntity> validate(Metadata metadata) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		if (Boolean.TRUE.equals(metadata.getAccessCondition().getConfidentiality().getGdprSensitive()) &&
				metadata.getExtMetadata().getExtSelfdata().getExtSelfdataContent() == null) {
			String errorMessage = String.format(IntegrationError.ERR_107.getMessage(), metadata.getGlobalId());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(IntegrationError.ERR_107.getCode(), errorMessage);

			integrationRequestsErrors.add(integrationRequestError);
		}

		if(Boolean.TRUE.equals(metadata.getAccessCondition().getConfidentiality().getGdprSensitive()) &&
				metadata.getExtMetadata().getExtSelfdata().getExtSelfdataContent() != null &&
				metadata.getExtMetadata().getExtSelfdata().getExtSelfdataContent().getSelfdataAccess() == SelfdataContent.SelfdataAccessEnum.API &&
				!selfdataMediaHelper.hasMandatoryMediasForAutomaticTreatment(metadata)
		) {
			String errorMessage = String.format(IntegrationError.ERR_305.getMessage(), metadata.getGlobalId());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(IntegrationError.ERR_305.getCode(), errorMessage);

			integrationRequestsErrors.add(integrationRequestError);
		}

		return integrationRequestsErrors;
	}

}

