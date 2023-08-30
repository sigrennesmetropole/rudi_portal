package org.rudi.microservice.kalim.service.integration.impl.validator;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LicenceValidatorUT {

	@InjectMocks
	private LicenceValidator licenceValidator;

	@Mock
	private LicenceStandardValidator licenceStandardValidator;

	@Mock
	private LicenceCustomValidator licenceCustomValidator;

	@Test
	@DisplayName("Test de la validation de la licence avec une licence vide")
	void testValidateLicenceWithEmptyValue() {
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				licenceValidator.validate(null);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		Assertions.assertTrue(integrationRequestErrorEntities.stream().anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.LICENCE.getLocalName())
								&& integrationRequestErrorEntity.getMessage().equals(
								String.format(IntegrationError.ERR_202.getMessage(),
										RudiMetadataField.LICENCE.getLocalName()))),
				"La valeur de la propriété " + RudiMetadataField.LICENCE.getLocalName() + " est manquante");
	}

	@Test
	@DisplayName("Test pour s'assurer qu'on passe bien da la méthode qui valide la licence standard")
	void testValidateLicenceStandard() {
		LicenceStandard licenceStandard = new LicenceStandard().licenceLabel(LicenceStandard.LicenceLabelEnum.PUBLIC_DOMAIN_CC0);
		licenceStandard.setLicenceType(Licence.LicenceTypeEnum.STANDARD);
		when(licenceStandardValidator.validate(licenceStandard)).thenReturn(Collections.emptySet());

		Set<IntegrationRequestErrorEntity> errors = licenceValidator.validate(licenceStandard);

		verify(licenceStandardValidator).validate(licenceStandard);
		assertThat(errors).isEmpty();

	}

	@Test
	@DisplayName("Test pour s'assurer qu'on passe bien da la méthode qui valide la licence custom")
	void testValidateLicenceCustom() {
		LicenceCustom licenceCustom = new LicenceCustom().customLicenceLabel(Collections.emptyList());
		licenceCustom.setLicenceType(Licence.LicenceTypeEnum.CUSTOM);
		when(licenceCustomValidator.validate(licenceCustom)).thenReturn(Collections.emptySet());

		Set<IntegrationRequestErrorEntity> errors = licenceValidator.validate(licenceCustom);

		verify(licenceCustomValidator).validate(licenceCustom);
		assertThat(errors).isEmpty();
	}
}
