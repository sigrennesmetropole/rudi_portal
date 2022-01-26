package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.LicenceCustom;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class LicenceCustomValidatorTest {

	@InjectMocks
	private LicenceCustomValidator licenceCustomValidator;

	@Test
	@DisplayName("Test de la validation d'une licence custom avec un label qui ne contient pas de traductions")
	public void testValidateLicenceCustomEmptyLabel() {
		LicenceCustom licenceCustom = new LicenceCustom().customLicenceLabel(Collections.emptyList());
		licenceCustom.setLicenceType(Licence.LicenceTypeEnum.CUSTOM);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				licenceCustomValidator.validate(licenceCustom);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		Assertions.assertTrue(integrationRequestErrorEntities.stream().anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.CUSTOM_LICENCE_LABEL.getLocalName())
								&& integrationRequestErrorEntity.getMessage().equals(
								String.format(IntegrationError.ERR_202.getMessage(),
										RudiMetadataField.CUSTOM_LICENCE_LABEL.getLocalName()))),
				"La valeur de la propriété " + RudiMetadataField.CUSTOM_LICENCE_LABEL.getLocalName() + " est manquante");
	}

	@Test
	@DisplayName("Test de la validation d'une licence custom avec un label qui ne contient pas de traductions")
	public void testValidateLicenceCustomNoErrors() {
		LicenceCustom licenceCustom = new LicenceCustom()
				.customLicenceLabel(List.of(new DictionaryEntry().lang(Language.FR).text("Licence custom")));
		licenceCustom.setLicenceType(Licence.LicenceTypeEnum.CUSTOM);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				licenceCustomValidator.validate(licenceCustom);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(0);
	}

}
