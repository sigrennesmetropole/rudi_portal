package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.kaccess.bean.Licence;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessConditionValidatorUT {

	@InjectMocks
	private AccessConditionValidator accessConditionValidator;

	@Mock
	private LicenceValidator licenceValidator;

	@Test
	@DisplayName("Test de la validation d'un objet AccessCondition vide")
	void testValidateAccessConditionWithEmptyValue() {
		Metadata metadata = new Metadata().accessCondition(null);
		Set<IntegrationRequestErrorEntity> integrationRequestErrorEntities =
				accessConditionValidator.validateMetadata(metadata);

		assertThat(integrationRequestErrorEntities.size()).isEqualTo(1);

		assertThat(integrationRequestErrorEntities).anyMatch(integrationRequestErrorEntity ->
						integrationRequestErrorEntity.getFieldName().equals(RudiMetadataField.ACCESS_CONDITION.getLocalName())
						&& integrationRequestErrorEntity.getMessage().equals(
								String.format(IntegrationError.ERR_202.getMessage(),
										RudiMetadataField.ACCESS_CONDITION.getLocalName())));
	}

	@Test
	@DisplayName("Test pour s'assurer de la validation de la licence d'un objet AccessCondition")
	void testValidateAccessConditionWithCheckingLicence() {
		MetadataAccessCondition accessCondition = new MetadataAccessCondition().licence(new Licence());
		when(licenceValidator.validate(accessCondition.getLicence())).thenReturn(Collections.emptySet());

		Set<IntegrationRequestErrorEntity> errors = accessConditionValidator.validate(accessCondition);

		verify(licenceValidator).validate(accessCondition.getLicence());
		assertThat(errors).isEmpty();
	}
}
