package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataMetadataInfo;
import org.rudi.facet.kaccess.constant.ConstantMetadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class MetadataInfoValidatorTest {

	@InjectMocks
	private MetadataInfoValidator metadataInfoValidator;

	@Test
	@DisplayName("Test de la validation d'un object MetadataMetadataInfo vide")
	void testValidateMetadataInfoEmptyValue() {
		Metadata metadata = new Metadata().metadataInfo(null);
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				metadataInfoValidator.validateMetadata(metadata);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		assertThat(integrationRequestErrorEntities).anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.METADATA_INFO.getLocalName())
					 && integrationRequestErrorEntity.getMessage().equals(
								String.format(IntegrationError.ERR_202.getMessage(),
										RudiMetadataField.METADATA_INFO.getLocalName())));
	}

	@Test
	@DisplayName("Test de la validation d'un object MetadataMetadataInfo avec une version d'api invalide")
	void testValidateMetadataInfoWithUnknownApiVersion() {
		final String metadataVersion = "2.1.0";
		final MetadataMetadataInfo metadataMetadataInfo = new MetadataMetadataInfo().apiVersion(metadataVersion);

		final Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				metadataInfoValidator.validate(metadataMetadataInfo);

		assertThat(integrationRequestErrorEntities).anyMatch(error ->
				error.getCode().equals(IntegrationError.ERR_106.getCode()) &&
						error.getMessage().equals(String.format(IntegrationError.ERR_106.getMessage(), metadataVersion, ConstantMetadata.CURRENT_METADATA_VERSION)) &&
						error.getFieldName().equals(RudiMetadataField.METADATA_INFO_API_VERSION.getLocalName()));
	}

	@Test
	@DisplayName("Test de la validation d'un object MetadataMetadataInfo valide")
	void testValidateMetadataInfoWithNoErrors() {
		final String metadataVersion = ConstantMetadata.CURRENT_METADATA_VERSION;
		final MetadataMetadataInfo metadataMetadataInfo = new MetadataMetadataInfo().apiVersion(metadataVersion);

		final Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				metadataInfoValidator.validate(metadataMetadataInfo);

		assertThat(integrationRequestErrorEntities).isEmpty();
	}
}
