package org.rudi.microservice.kalim.service.integration.impl.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.bean.ProgressStatus;
import org.rudi.microservice.kalim.service.IntegrationError;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.DatasetCreatorIsAuthenticatedValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.MetadataInfoProviderIsAuthenticatedValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostIntegrationRequestTreatmentHandlerTest {

	private final ObjectMapper objectMapper = new DefaultJackson2ObjectMapperBuilder().build();
	private final Error500Builder error500Builder = new Error500Builder();
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private IntegrationRequestTreatmentHandler handler;
	@Mock
	private AbstractMetadataValidator<?> validator;
	@Mock
	private DatasetService datasetService;
	@Mock
	private APIManagerHelper apiManagerHelper;
	@Mock
	private MetadataInfoProviderIsAuthenticatedValidator metadataInfoProviderIsAuthenticatedValidator;
	@Mock
	private DatasetCreatorIsAuthenticatedValidator datasetCreatorIsAuthenticatedValidator;
	@Mock
	private OrganizationHelper organizationHelper;
	@Captor
	private ArgumentCaptor<Metadata> metadataArgumentCaptor;

	@BeforeEach
	void setUp() {
		handler = new PostIntegrationRequestTreatmentHandler(
				datasetService,
				apiManagerHelper,
				objectMapper,
				Collections.singletonList(validator),
				error500Builder,
				metadataInfoProviderIsAuthenticatedValidator,
				datasetCreatorIsAuthenticatedValidator,
				organizationHelper);

		when(validator.canBeUsedBy(handler)).thenReturn(true);
	}

	private Metadata buildMetadataToCreate() throws IOException {
		return jsonResourceReader.read("metadata/create-ok.json", Metadata.class);
	}

	@Test
	@DisplayName("validation failed ❌ ⇒ stop \uD83D\uDED1")
		// RUDI-628
	void createIntegrationRequestValidationErrorNoInteractions() throws IOException {

		final Metadata metadata = buildMetadataToCreate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadata);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.POST)
				.uuid(UUID.randomUUID())
				.globalId(metadata.getGlobalId())
				.progressStatus(ProgressStatus.CREATED)
				.file(metadataJson)
				.errors(new HashSet<>())
				.build();

		final Set<IntegrationRequestErrorEntity> errors = Collections.singleton(new IntegrationRequestErrorEntity());
		when(validator.validateMetadata(any(Metadata.class))).thenReturn(errors);

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).isEqualTo(IntegrationStatus.KO);

		// If validation fails, integration request should not go any further
		verifyNoInteractions(datasetService);
		verifyNoInteractions(apiManagerHelper);
	}

	@Test
	@DisplayName("validation passed ✔ ⇒ dataset and API created \uD83E\uDD73")
	void createIntegrationRequestNoValidationErrorInteractions() throws DataverseAPIException, APIManagerException, IOException {

		final Metadata metadata = buildMetadataToCreate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadata);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.POST)
				.uuid(UUID.randomUUID())
				.globalId(metadata.getGlobalId())
				.progressStatus(ProgressStatus.CREATED)
				.file(metadataJson)
				.errors(new HashSet<>())
				.build();

		final Set<IntegrationRequestErrorEntity> errors = Collections.emptySet();
		when(validator.validateMetadata(metadataArgumentCaptor.capture())).thenReturn(errors);

		final String createdMetadataDoi = "DOI";
		when(datasetService.createDataset(metadata)).thenReturn(createdMetadataDoi);
		final Metadata createdMetadata = mock(Metadata.class);
		when(datasetService.getDataset(createdMetadataDoi)).thenReturn(createdMetadata);

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).isEqualTo(IntegrationStatus.OK);

		// If validation succeeds, corresponding API should be created
		verify(apiManagerHelper).createAPI(integrationRequest, createdMetadata);
	}

	@Test
	@DisplayName("getDataset NullPointerException ❌ ⇒ dataset creation cancelled")
	void createIntegrationRequestGetDatasetError() throws DataverseAPIException, IOException {

		final Metadata metadata = buildMetadataToCreate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadata);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.POST)
				.uuid(UUID.randomUUID())
				.globalId(metadata.getGlobalId())
				.progressStatus(ProgressStatus.CREATED)
				.file(metadataJson)
				.errors(new HashSet<>())
				.build();

		final Set<IntegrationRequestErrorEntity> errors = Collections.emptySet();
		when(validator.validateMetadata(metadataArgumentCaptor.capture())).thenReturn(errors);

		final String createdMetadataDoi = "DOI";
		when(datasetService.createDataset(metadata)).thenReturn(createdMetadataDoi);
		when(datasetService.getDataset(createdMetadataDoi)).thenThrow(new NullPointerException());

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).isEqualTo(IntegrationStatus.KO);
		assertThat(integrationRequest.getErrors()).as("RUDI-773 : On reçoit uniquement une erreur")
				.hasSize(1)
				.allSatisfy(error -> assertThat(error)
						.hasFieldOrPropertyWithValue("code", IntegrationError.ERR_500.getCode())
						.hasFieldOrPropertyWithValue("message", IntegrationError.ERR_500.getMessage())
				);

		// If getDataset failed, API Manager should never but called
		verifyNoMoreInteractions(apiManagerHelper);

		// And Dataset should be deleted
		verify(datasetService).deleteDataset(createdMetadataDoi);
	}

	@Test
	@DisplayName("WSO2 error ❌ ⇒ dataset creation cancelled")
	void createIntegrationRequestWso2Errors() throws DataverseAPIException, APIManagerException, IOException {

		final Metadata metadata = buildMetadataToCreate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadata);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.POST)
				.uuid(UUID.randomUUID())
				.globalId(metadata.getGlobalId())
				.progressStatus(ProgressStatus.CREATED)
				.file(metadataJson)
				.errors(new HashSet<>())
				.build();

		final Set<IntegrationRequestErrorEntity> errors = Collections.emptySet();
		when(validator.validateMetadata(metadataArgumentCaptor.capture())).thenReturn(errors);

		final String createdMetadataDoi = "DOI";
		when(datasetService.createDataset(metadata)).thenReturn(createdMetadataDoi);
		final Metadata createdMetadata = mock(Metadata.class);
		when(datasetService.getDataset(createdMetadataDoi)).thenReturn(createdMetadata);
		when(createdMetadata.getGlobalId()).thenReturn(metadata.getGlobalId());

		doThrow(new APIManagerException("Erreur test")).when(apiManagerHelper).createAPI(eq(integrationRequest), any());

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus())
				.as("L'intégration est KO car WSO a renvoyé une erreur")
				.isEqualTo(IntegrationStatus.KO);
		verify(datasetService, description("Just created Dataset should be deleted at once")).deleteDataset(metadata.getGlobalId());
	}

	@Test
	@DisplayName("Exception when validate ⇒ KO")
	void createIntegrationRequestExceptionWhenValidate() throws IOException {

		final Metadata metadataToCreate = buildMetadataToCreate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadataToCreate);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.POST)
				.uuid(UUID.randomUUID())
				.progressStatus(ProgressStatus.CREATED)
				.errors(new HashSet<>())
				.file(metadataJson)
				.build();

		final Exception e = new NotImplementedException("Sample exception");
		when(validator.validateMetadata(metadataArgumentCaptor.capture())).thenThrow(e);

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).isEqualTo(IntegrationStatus.KO);

		// No interactions
		verifyNoInteractions(datasetService);
		verifyNoInteractions(apiManagerHelper);
	}
}
