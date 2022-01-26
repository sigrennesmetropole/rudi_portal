package org.rudi.microservice.kalim.service.integration.impl.validator;

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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LicenceStandardValidatorTest {

	@InjectMocks
	private LicenceStandardValidator licenceStandardValidator;

	@Mock
	private KOSHelper kosHelper;

	@Test
	@DisplayName("Test de la validation d'une licence standard avec un label vide")
	public void testValidateLicenceStandardEmptyLabel() {
		LicenceStandard licenceStandard = new LicenceStandard().licenceLabel("");
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
	@DisplayName("Test de la validation d'une licence standard avec un label invalide")
	public void testValidateLicenceStandardInvalidLabel() {
		LicenceStandard licenceStandard = new LicenceStandard().licenceLabel("licence-invalide");
		licenceStandard.setLicenceType(Licence.LicenceTypeEnum.STANDARD);

		when(kosHelper.skosConceptLicenceExists(licenceStandard.getLicenceLabel())).thenReturn(false);

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
	public void testValidateLicenceStandardNoErrors() {
		LicenceStandard licenceStandard = new LicenceStandard().licenceLabel("open-source-licence");
		licenceStandard.setLicenceType(Licence.LicenceTypeEnum.STANDARD);

		when(kosHelper.skosConceptLicenceExists(licenceStandard.getLicenceLabel())).thenReturn(true);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				licenceStandardValidator.validate(licenceStandard);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(0);
	}
}
