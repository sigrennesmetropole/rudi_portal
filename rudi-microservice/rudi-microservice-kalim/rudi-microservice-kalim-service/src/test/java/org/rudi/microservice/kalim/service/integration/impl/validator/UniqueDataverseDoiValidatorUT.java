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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueDataverseDoiValidatorUT {
	private final DataverseDoiExtractor fieldExtractor = new DataverseDoiExtractor();
	private UniqueDataverseDoiValidator validator;
	@Mock
	private DatasetService datasetService;

	@BeforeEach
	void setUp() {
		validator = new UniqueDataverseDoiValidator(fieldExtractor, datasetService);
	}

	@Test
	void validateNotAlreadyUsed() throws DataverseAPIException {
		final Metadata metadata = new Metadata()
				.dataverseDoi("doi:10.5072/FK2/OFKEB1");

		when(datasetService.datasetExists(metadata.getDataverseDoi())).thenReturn(false);

		final Set<IntegrationRequestErrorEntity> errors = validator.validate(metadata);

		assertThat(errors).isEmpty();
	}

	@Test
	void validateAlreadyUsed() throws DataverseAPIException {
		final Metadata metadata = new Metadata()
				.dataverseDoi("doi:10.5072/FK2/OFKEB1");

		when(datasetService.datasetExists(metadata.getDataverseDoi())).thenReturn(true);

		final Set<IntegrationRequestErrorEntity> errors = validator.validate(metadata);

		assertThat(errors).hasOnlyOneElementSatisfying(error -> assertThat(error)
				.hasFieldOrPropertyWithValue("code", "ERR-304")
				.hasFieldOrPropertyWithValue("message", "La valeur saisie 'doi:10.5072/FK2/OFKEB1' pour le champ 'dataverse_doi' est déjà utilisée")
				.hasFieldOrPropertyWithValue("fieldName", "dataverse_doi")
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
