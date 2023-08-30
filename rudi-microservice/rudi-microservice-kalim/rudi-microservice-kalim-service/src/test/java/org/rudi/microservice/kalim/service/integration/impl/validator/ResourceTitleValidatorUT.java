package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResourceTitleValidatorUT {

	@InjectMocks
	private ResourceTitleValidator resourceTitleValidator;

	@Test
	@DisplayName("Test de la validation titre du jdd avec une valeur vide")
	void testValidateResourceTitleWithEmptyValue() {
		Metadata metadata = new Metadata().resourceTitle("");
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				resourceTitleValidator.validateMetadata(metadata);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		assertThat(integrationRequestErrorEntities).anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.RESOURCE_TITLE.getLocalName())
								&& integrationRequestErrorEntity.getMessage().equals(
								String.format(IntegrationError.ERR_202.getMessage(),
										RudiMetadataField.RESOURCE_TITLE.getLocalName())));
	}

	@Test
	@DisplayName("Test de la validation titre du jdd avec une valeur valide")
	void testValidateResourceTitleWithNoErrors() {
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				resourceTitleValidator.validate("Test validate");

		assertThat(integrationRequestErrorEntities).isEmpty();
	}
}
