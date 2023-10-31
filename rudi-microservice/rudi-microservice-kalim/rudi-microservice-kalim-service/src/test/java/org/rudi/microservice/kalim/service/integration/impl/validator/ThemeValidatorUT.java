package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kos.helper.KosHelper;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThemeValidatorUT {

	@InjectMocks
	private ThemeValidator themeValidator;

	@Mock
	private KosHelper kosHelper;

	@Test
	@DisplayName("Test de la validation d'un thème inconnu dans skos")
	public void testValidateUnknownThem() {
		String themeToValidate = "invalid-theme";

		when(kosHelper.skosConceptThemeExists(themeToValidate)).thenReturn(false);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				themeValidator.validate(themeToValidate);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		assertThat(integrationRequestErrorEntities).anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.THEME.getLocalName())
					 && integrationRequestErrorEntity.getMessage().equals(String.format(IntegrationError.ERR_303.getMessage(),
										themeToValidate,
										RudiMetadataField.THEME.getLocalName(),
										"un code de concept SKOS connu")));
	}

	@Test
	@DisplayName("Test de la validation d'un thème connu")
	public void testValidateExistingTheme() {
		String themeToValidate = "agriculture";

		when(kosHelper.skosConceptThemeExists(themeToValidate)).thenReturn(true);

		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				themeValidator.validate(themeToValidate);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(0);
	}
}
