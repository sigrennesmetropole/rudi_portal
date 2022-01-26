package org.rudi.microservice.kalim.service.integration.impl.validator;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kos.helper.KOSHelper;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class LicenceStandardValidator implements ElementValidator<LicenceStandard> {

	private final KOSHelper kosHelper;

	@Override
	public Set<IntegrationRequestErrorEntity> validate(LicenceStandard licenceStandard) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		final String licenceLabel = licenceStandard.getLicenceLabel();
		if (StringUtils.isEmpty(licenceLabel)) {
			String errorMessage = String.format(IntegrationError.ERR_202.getMessage(), RudiMetadataField.LICENCE_LABEL.getLocalName());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_202.getCode(), errorMessage, RudiMetadataField.LICENCE_LABEL.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		} else {
			CollectionUtils.addIgnoreNull(integrationRequestsErrors, validateLicenceSkosConceptCode(licenceLabel));
		}
		return integrationRequestsErrors;
	}

	private IntegrationRequestErrorEntity validateLicenceSkosConceptCode(String licenceLabel) {
		if (!kosHelper.skosConceptLicenceExists(licenceLabel)) {
			return new Error303Builder()
					.field(RudiMetadataField.LICENCE_LABEL)
					.fieldValue(licenceLabel)
					.expectedString("un code de concept SKOS connu")
					.build();
		}
		return null;
	}
}
