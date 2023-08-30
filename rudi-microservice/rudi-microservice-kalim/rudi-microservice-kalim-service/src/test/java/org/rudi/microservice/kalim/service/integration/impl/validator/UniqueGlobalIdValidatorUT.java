package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueGlobalIdValidatorUT {
	private final GlobalIdExtractor fieldExtractor = new GlobalIdExtractor();
	private UniqueGlobalIdValidator validator;
	@Mock
	private DatasetService datasetService;

	@BeforeEach
	void setUp() {
		validator = new UniqueGlobalIdValidator(fieldExtractor, datasetService);
	}

	@Test
	void validateNotAlreadyUsed() throws DataverseAPIException {
		final Metadata metadata = new Metadata()
				.globalId(UUID.randomUUID());

		when(datasetService.datasetExists(metadata.getGlobalId())).thenReturn(false);

		final Set<IntegrationRequestErrorEntity> errors = validator.validate(metadata);

		assertThat(errors).isEmpty();
	}

	@Test
	void validateAlreadyUsed() throws DataverseAPIException {
		final Metadata metadata = new Metadata()
				.globalId(UUID.fromString("c89a88e0-878c-4693-9e67-ffb76c1adc84"));

		when(datasetService.datasetExists(metadata.getGlobalId())).thenReturn(true);

		final Set<IntegrationRequestErrorEntity> errors = validator.validate(metadata);

		assertThat(errors).hasOnlyOneElementSatisfying(error -> assertThat(error)
				.hasFieldOrPropertyWithValue("code", "ERR-304")
				.hasFieldOrPropertyWithValue("message", "La valeur saisie 'c89a88e0-878c-4693-9e67-ffb76c1adc84' pour le champ 'global_id' est déjà utilisée")
				.hasFieldOrPropertyWithValue("fieldName", "global_id")
		);
	}

	@Test
	void validateNull() {
		final Metadata metadata = new Metadata();

		final Set<IntegrationRequestErrorEntity> errors = validator.validate(metadata);

		assertThat(errors).isEmpty();

		verifyNoInteractions(datasetService);
	}

}
