package org.rudi.microservice.kalim.service.integration.impl.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
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
import org.rudi.microservice.kalim.service.helper.ApiManagerHelper;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.DatasetCreatorIsAuthenticatedValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.MetadataInfoProviderIsAuthenticatedValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class PutIntegrationRequestTreatmentHandlerUT {

	private final ObjectMapper objectMapper = new DefaultJackson2ObjectMapperBuilder().build();
	private final Error500Builder error500Builder = new Error500Builder();
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private AbstractIntegrationRequestTreatmentHandler handler;
	@Mock
	private AbstractMetadataValidator<?> validator;
	@Mock
	private DatasetService datasetService;
	@Mock
	private APIManagerHelper apiManagerHelper;
	@Mock
	private ApiManagerHelper apiGatewayManagerHelper;
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
		handler = new PutIntegrationRequestTreatmentHandler(datasetService, apiGatewayManagerHelper, apiManagerHelper,
				objectMapper, Collections.singletonList(validator), error500Builder,
				metadataInfoProviderIsAuthenticatedValidator, datasetCreatorIsAuthenticatedValidator,
				organizationHelper);

		when(validator.canBeUsedBy(handler)).thenReturn(true);
	}

	@Test
	@DisplayName("validation failed ❌ ⇒ stop \uD83D\uDED1")
	void handleValidationErrorNoUpdate() throws IOException {

		final Metadata metadataToUpdate = buildMetadataToUpdate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadataToUpdate);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder().method(Method.PUT)
				.uuid(UUID.randomUUID()).globalId(metadataToUpdate.getGlobalId()).progressStatus(ProgressStatus.CREATED)
				.file(metadataJson).errors(new HashSet<>()).build();

		final Set<IntegrationRequestErrorEntity> errors = Collections.singleton(new IntegrationRequestErrorEntity());
		when(validator.validateMetadata(any(Metadata.class))).thenReturn(errors);

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).isEqualTo(IntegrationStatus.KO);

		// If validation fails, integration request should not go any further
		verifyNoMoreInteractions(datasetService);
		verifyNoInteractions(apiManagerHelper);
	}

	@Test
	@DisplayName("validation passed ✔ ⇒ dataset and API updated \uD83E\uDD73")
	void handleNoValidationErrorUpdate() throws DataverseAPIException, APIManagerException, IOException {

		final Metadata metadataToUpdate = buildMetadataToUpdate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadataToUpdate);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder().method(Method.PUT)
				.uuid(UUID.randomUUID()).globalId(metadataToUpdate.getGlobalId()).progressStatus(ProgressStatus.CREATED)
				.file(metadataJson).errors(new HashSet<>()).build();

		final Set<IntegrationRequestErrorEntity> errors = Collections.emptySet();
		when(validator.validateMetadata(metadataArgumentCaptor.capture())).thenReturn(errors);

		final Metadata updatedMetadata = mock(Metadata.class);
		when(datasetService.getDataset(metadataToUpdate.getGlobalId())).thenReturn(metadataToUpdate);
		when(datasetService.updateDataset(metadataToUpdate)).thenReturn(updatedMetadata);

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).isEqualTo(IntegrationStatus.OK);

		// If validation succeeds, API should be updated
		verify(apiManagerHelper).updateAPI(integrationRequest, updatedMetadata, metadataToUpdate);
	}

	@Test
	@DisplayName("non existing metadata ⇒ error \uD83D\uDED1")
	void handleNonExistingMetadata() throws IOException {

		final Metadata metadataToUpdate = buildMetadataToUpdate();
		final String metadataJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadataToUpdate);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder().method(Method.PUT)
				.uuid(UUID.randomUUID()).globalId(metadataToUpdate.getGlobalId()).progressStatus(ProgressStatus.CREATED)
				.file(metadataJson).errors(new HashSet<>()).build();

		final IntegrationRequestErrorEntity nonExistingError = mock(IntegrationRequestErrorEntity.class);
		final Set<IntegrationRequestErrorEntity> errors = Collections.singleton(nonExistingError);
		when(validator.validateMetadata(metadataArgumentCaptor.capture())).thenReturn(errors);

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).isEqualTo(IntegrationStatus.KO);

		// If validation fails, there is no interaction
		verifyNoInteractions(datasetService);
		verifyNoInteractions(apiManagerHelper);
	}

	@Test
	@DisplayName("WSO2 error ❌ ⇒ dataset updated rollback")
	void handleValidationWSO2ErrorNoUpdate() throws DataverseAPIException, APIManagerException, IOException {

		final Metadata actualMetadata = buildMetadataBeforeUpdate();
		final Metadata metadataToUpdate = buildMetadataToUpdate();
		final String metadataToUpdateJson = jsonResourceReader.getObjectMapper().writeValueAsString(metadataToUpdate);
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder().method(Method.PUT)
				.uuid(UUID.randomUUID()).globalId(metadataToUpdate.getGlobalId()).progressStatus(ProgressStatus.CREATED)
				.file(metadataToUpdateJson).errors(new HashSet<>()).build();

		final Set<IntegrationRequestErrorEntity> errors = Collections.emptySet();
		when(validator.validateMetadata(any(Metadata.class))).thenReturn(errors);

		when(datasetService.getDataset(metadataToUpdate.getGlobalId())).thenReturn(actualMetadata);
		when(datasetService.updateDataset(metadataToUpdate)).thenReturn(metadataToUpdate);
		when(datasetService.updateDataset(actualMetadata)).thenReturn(actualMetadata);

		doThrow(new APIManagerException("Erreur test")).when(apiManagerHelper).updateAPI(eq(integrationRequest), any(),
				any());

		handler.handle(integrationRequest);

		assertThat(integrationRequest.getIntegrationStatus()).as("L'intégration est KO car WSO a renvoyé une erreur")
				.isEqualTo(IntegrationStatus.KO);

		InOrder inOrderToVerifyDatasetServiceUpdateCall = inOrder(datasetService);
		// Appel de la mise à jour des métadonnées
		inOrderToVerifyDatasetServiceUpdateCall.verify(datasetService).updateDataset(metadataToUpdate);
		// Appel de la mise à jour pour restaurer les métadonnées
		inOrderToVerifyDatasetServiceUpdateCall.verify(datasetService).updateDataset(actualMetadata);
	}

	private Metadata buildMetadataBeforeUpdate() throws IOException {
		return jsonResourceReader.read("metadata/create-ok.json", Metadata.class);
	}

	private Metadata buildMetadataToUpdate() throws IOException {
		return jsonResourceReader.read("metadata/update-ok.json", Metadata.class);
	}

}
