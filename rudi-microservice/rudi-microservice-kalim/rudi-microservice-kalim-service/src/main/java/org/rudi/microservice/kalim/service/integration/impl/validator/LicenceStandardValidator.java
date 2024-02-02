package org.rudi.microservice.kalim.service.integration.impl.validator;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kos.helper.KosHelper;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
class LicenceStandardValidator implements ElementValidator<LicenceStandard> {

	private final KosHelper kosHelper;

	@Override
	public Set<IntegrationRequestErrorEntity> validate(LicenceStandard licenceStandard) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		// Le label étant required par le contrat d'interface, il ne peut pas être null.
		final LicenceStandard.LicenceLabelEnum licenceLabel = licenceStandard.getLicenceLabel();
		CollectionUtils.addIgnoreNull(integrationRequestsErrors, validateLicenceSkosConceptCode(licenceLabel.toString()));
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
