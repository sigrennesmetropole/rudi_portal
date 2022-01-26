package org.rudi.microservice.kalim.service.integration.impl.validator;

import lombok.RequiredArgsConstructor;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Component class LicenceValidator implements ElementValidator<Licence> {

	private final LicenceStandardValidator licenceStandardValidator;
	private final LicenceCustomValidator licenceCustomValidator;

	/**
	 * Validation de l'attribut licence de access_condition
	 *
	 * @param licence 		Licence
	 * @return				liste des erreurs
	 */
	@Override
	public Set<IntegrationRequestErrorEntity> validate(Licence licence) {
		Set<IntegrationRequestErrorEntity> integrationRequestsErrors = new HashSet<>();
		if (licence == null) {
			String errorMessage = String.format(IntegrationError.ERR_202.getMessage(), RudiMetadataField.LICENCE.getLocalName());
			IntegrationRequestErrorEntity integrationRequestError = new IntegrationRequestErrorEntity(
					UUID.randomUUID(), IntegrationError.ERR_202.getCode(), errorMessage, RudiMetadataField.LICENCE.getLocalName(), LocalDateTime.now());

			integrationRequestsErrors.add(integrationRequestError);
		} else {
			if (licence.getLicenceType() == Licence.LicenceTypeEnum.STANDARD) {
				integrationRequestsErrors.addAll(licenceStandardValidator.validate((LicenceStandard) licence));
			} else if (licence.getLicenceType() == Licence.LicenceTypeEnum.CUSTOM) {
				integrationRequestsErrors.addAll(licenceCustomValidator.validate((LicenceCustom) licence));
			}
		}
		return integrationRequestsErrors;
	}

}
