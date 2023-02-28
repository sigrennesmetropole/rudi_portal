package org.rudi.microservice.kalim.service.integration.impl.validator;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kos.helper.KOSHelper;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LicenceStandardValidatorTest {

	@InjectMocks
	private LicenceStandardValidator licenceStandardValidator;

	@Mock
	private KOSHelper kosHelper;

	@Test
	@DisplayName("Test de la validation d'une licence standard avec un label vide")
	void testValidateLicenceStandardEmptyLabel() {
		LicenceStandard licenceStandard = new LicenceStandard().licenceLabel(null);
		licenceStandard.setLicenceType(Licence.LicenceTypeEnum.STANDARD);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				licenceStandardValidator.validate(licenceStandard);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		Assertions.assertTrue(integrationRequestErrorEntities.stream().anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.LICENCE_LABEL.getLocalName())
								&& integrationRequestErrorEntity.getMessage().equals(
								String.format(IntegrationError.ERR_202.getMessage(),
										RudiMetadataField.LICENCE_LABEL.getLocalName()))),
				"La valeur de la propriété " + RudiMetadataField.LICENCE_LABEL.getLocalName() + " est manquante");
	}

	@Test
	@DisplayName("Test de la validation d'une licence standard avec un label inconnu de kos")
	void testValidateLicenceStandardInvalidLabel() {
		final var unknownLicenceLabel =  LicenceStandard.LicenceLabelEnum.CC_BY_ND_4_0;
		LicenceStandard licenceStandard = new LicenceStandard().licenceLabel(unknownLicenceLabel);
		licenceStandard.setLicenceType(Licence.LicenceTypeEnum.STANDARD);

		when(kosHelper.skosConceptLicenceExists(unknownLicenceLabel.toString())).thenReturn(false);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				licenceStandardValidator.validate(licenceStandard);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		Assertions.assertTrue(integrationRequestErrorEntities.stream().anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.LICENCE_LABEL.getLocalName())
								&& integrationRequestErrorEntity.getMessage().equals(
								String.format(IntegrationError.ERR_303.getMessage(),
										licenceStandard.getLicenceLabel(),
										RudiMetadataField.LICENCE_LABEL.getLocalName(),
										"un code de concept SKOS connu"))),
				"La valeur de la propriété " + RudiMetadataField.LICENCE_LABEL.getLocalName() +
						" ne correspond pas è un concept de licence connu");
	}

	@Test
	@DisplayName("Test de la validation d'une licence standard avec aucune erreur")
	void testValidateLicenceStandardNoErrors() {
		final var knownLicenceLabel =  LicenceStandard.LicenceLabelEnum.PUBLIC_DOMAIN_CC0;
		LicenceStandard licenceStandard = new LicenceStandard().licenceLabel(knownLicenceLabel);
		licenceStandard.setLicenceType(Licence.LicenceTypeEnum.STANDARD);

		when(kosHelper.skosConceptLicenceExists(knownLicenceLabel.toString())).thenReturn(true);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				licenceStandardValidator.validate(licenceStandard);

		assertThat(integrationRequestErrorEntities).isEmpty();
	}
}
