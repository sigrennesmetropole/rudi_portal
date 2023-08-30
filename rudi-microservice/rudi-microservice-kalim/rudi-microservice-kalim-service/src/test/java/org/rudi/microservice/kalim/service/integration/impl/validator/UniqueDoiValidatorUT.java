package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueDoiValidatorUT {
	private final DoiExtractor fieldExtractor = new DoiExtractor();
	private UniqueDoiValidator validator;
	@Mock
	private DatasetService datasetService;

	@BeforeEach
	void setUp() {
		validator = new UniqueDoiValidator(fieldExtractor, datasetService);
	}

	@Test
	void validateNotAlreadyUsed() throws DataverseAPIException {
		final Metadata metadata = new Metadata()
				.doi("10.5072/FK2/OFKEB1");

		when(datasetService.datasetExists(new DatasetSearchCriteria().doi(metadata.getDoi()))).thenReturn(false);

		final Set<IntegrationRequestErrorEntity> errors = validator.validate(metadata);

		assertThat(errors).isEmpty();
	}

	@Test
	void validateAlreadyUsed() throws DataverseAPIException {
		final Metadata metadata = new Metadata()
				.doi("10.5072/FK2/OFKEB1");

		when(datasetService.datasetExists(new DatasetSearchCriteria().doi(metadata.getDoi()))).thenReturn(true);

		final Set<IntegrationRequestErrorEntity> errors = validator.validate(metadata);

		assertThat(errors).hasOnlyOneElementSatisfying(error -> assertThat(error)
				.hasFieldOrPropertyWithValue("code", "ERR-304")
				.hasFieldOrPropertyWithValue("message", "La valeur saisie '10.5072/FK2/OFKEB1' pour le champ 'doi' est déjà utilisée")
				.hasFieldOrPropertyWithValue("fieldName", "doi")
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
