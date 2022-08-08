package org.rudi.microservice.kalim.service.integration.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.bean.ProgressStatus;
import org.rudi.microservice.kalim.core.bean.Report;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.helper.provider.KalimProviderHelper;
import org.rudi.microservice.kalim.service.integration.impl.handlers.DeleteIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PostIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PutIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.mapper.IntegrationRequestMapper;
import org.rudi.microservice.kalim.service.mapper.IntegrationRequestMapperImpl;
import org.rudi.microservice.kalim.service.mapper.ReportErrorMapper;
import org.rudi.microservice.kalim.service.mapper.ReportErrorMapperImpl;
import org.rudi.microservice.kalim.storage.dao.integration.IntegrationRequestCustomDao;
import org.rudi.microservice.kalim.storage.dao.integration.IntegrationRequestDao;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IntegrationRequestServiceImplTest {

	private final ObjectMapper objectMapper = new DefaultJackson2ObjectMapperBuilder().build();
	private final IntegrationRequestMapper integrationRequestMapper = new IntegrationRequestMapperImpl();
	private final ReportErrorMapper reportErrorMapper = new ReportErrorMapperImpl();
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private IntegrationRequestServiceImpl integrationRequestService;
	@Mock
	private IntegrationRequestCustomDao integrationRequestCustomDao;
	@Mock
	private IntegrationRequestDao integrationRequestDao;
	@Mock
	private PostIntegrationRequestTreatmentHandler postHandler;
	@Mock
	private PutIntegrationRequestTreatmentHandler putHandler;
	@Mock
	private DeleteIntegrationRequestTreatmentHandler deleteHandler;
	@Mock
	private ProviderHelper providerHelper;
	@Mock
	private KalimProviderHelper kalimProviderHelper;
	@Captor
	private ArgumentCaptor<IntegrationRequestEntity> integrationRequestEntityToCreateCaptor;
	@Captor
	private ArgumentCaptor<Report> reportCaptor;

	@BeforeEach
	void setUp() {
		integrationRequestService = new IntegrationRequestServiceImpl(
				objectMapper,
				integrationRequestMapper, reportErrorMapper,
				integrationRequestDao, integrationRequestCustomDao,
				kalimProviderHelper, providerHelper, null,
				postHandler, putHandler, deleteHandler
		);
	}

	@Test
	void handleIntegrationRequest_post() {
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.POST)
				.uuid(UUID.randomUUID())
				.globalId(UUID.randomUUID())
				.progressStatus(ProgressStatus.CREATED)
				.errors(new HashSet<>())
				.build();

		when(integrationRequestCustomDao.findByUUIDAndLock(integrationRequest.getUuid())).thenReturn(integrationRequest);

		integrationRequestService.handleIntegrationRequest(integrationRequest.getUuid());

		assertThat(integrationRequest).hasFieldOrPropertyWithValue("progressStatus", ProgressStatus.INTEGRATION_HANDLED);
		assertThat(integrationRequest.getTreatmentDate()).isNotNull();

		verify(postHandler).handle(integrationRequest);
	}

	@Test
	void handleIntegrationRequest_put() {
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.PUT)
				.uuid(UUID.randomUUID())
				.globalId(UUID.randomUUID())
				.progressStatus(ProgressStatus.CREATED)
				.errors(new HashSet<>())
				.build();

		when(integrationRequestCustomDao.findByUUIDAndLock(integrationRequest.getUuid())).thenReturn(integrationRequest);

		integrationRequestService.handleIntegrationRequest(integrationRequest.getUuid());

		assertThat(integrationRequest).hasFieldOrPropertyWithValue("progressStatus", ProgressStatus.INTEGRATION_HANDLED);
		assertThat(integrationRequest.getTreatmentDate()).isNotNull();

		verify(putHandler).handle(integrationRequest);
	}

	@Test
	void handleIntegrationRequest_delete() {
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.method(Method.DELETE)
				.uuid(UUID.randomUUID())
				.globalId(UUID.randomUUID())
				.progressStatus(ProgressStatus.CREATED)
				.errors(new HashSet<>())
				.build();

		when(integrationRequestCustomDao.findByUUIDAndLock(integrationRequest.getUuid())).thenReturn(integrationRequest);

		integrationRequestService.handleIntegrationRequest(integrationRequest.getUuid());

		assertThat(integrationRequest).hasFieldOrPropertyWithValue("progressStatus", ProgressStatus.INTEGRATION_HANDLED);
		assertThat(integrationRequest.getTreatmentDate()).isNotNull();

		verify(deleteHandler).handle(integrationRequest);
	}

	@Test
	void createIntegrationRequestFromHarvesting() throws IntegrationException, JsonProcessingException {
		final Metadata metadataToCreate = new Metadata()
				.globalId(UUID.randomUUID())
				.resourceTitle("JDD à créer");
		final NodeProvider harvestedNode = new NodeProvider()
				.uuid(UUID.randomUUID())
				.version("V1");
		final Method method = Method.POST;

		when(integrationRequestDao.save(integrationRequestEntityToCreateCaptor.capture())).then(invocation -> invocation.getArgument(0));

		final IntegrationRequest integrationRequest = integrationRequestService.createIntegrationRequestFromHarvesting(metadataToCreate, method, harvestedNode);

		final IntegrationRequestEntity createdIntegrationRequestEntity = integrationRequestEntityToCreateCaptor.getValue();
		assertThat(createdIntegrationRequestEntity)
				.hasNoNullFieldsOrPropertiesExcept("treatmentDate", "sendRequestDate", "errors", "id")
				.hasFieldOrProperty("uuid")
				.hasFieldOrPropertyWithValue("method", method)
				.hasFieldOrProperty("submissionDate")
				.hasFieldOrPropertyWithValue("resourceTitle", metadataToCreate.getResourceTitle())
				.hasFieldOrPropertyWithValue("globalId", metadataToCreate.getGlobalId())
				.hasFieldOrPropertyWithValue("progressStatus", ProgressStatus.CREATED)
				.hasFieldOrPropertyWithValue("integrationStatus", IntegrationStatus.KO)
				.hasFieldOrPropertyWithValue("nodeProviderId", harvestedNode.getUuid())
				.hasFieldOrPropertyWithValue("version", harvestedNode.getVersion())
				.hasFieldOrPropertyWithValue("file", jsonResourceReader.getObjectMapper().writeValueAsString(metadataToCreate))
		;

		assertThat(integrationRequest).isEqualToIgnoringGivenFields(createdIntegrationRequestEntity, "nodeProviderId", "file", "submissionDate");
		assertThat(createdIntegrationRequestEntity.getSubmissionDate()).isEqualToIgnoringHours(integrationRequest.getSubmissionDate().atStartOfDay());
	}

	@ParameterizedTest
	@CsvSource({
			"POST,l’intégration du jeu de données Test de rapport minoritaire s’est bien déroulée le ",
			"PUT,la modification du jeu de données Test de rapport minoritaire s’est bien déroulée le ",
			"DELETE,la suppression du jeu de données Test de rapport minoritaire s’est bien déroulée le ",
	})
	void handleIntegrationRequestReport(final String methodString, final String expectedMessageBeginning) {
		final IntegrationRequestEntity integrationRequest = IntegrationRequestEntity.builder()
				.progressStatus(ProgressStatus.INTEGRATION_HANDLED)
				.uuid(UUID.randomUUID())
				.globalId(UUID.randomUUID())
				.treatmentDate(LocalDateTime.now())
				.method(Method.fromValue(methodString))
				.resourceTitle("Test de rapport minoritaire")
				.version("v1")
				.integrationStatus(IntegrationStatus.OK)
				.errors(new HashSet<>())
				.submissionDate(LocalDateTime.now())
				.submittedByHarvesting(true)
				.build();
		final NodeProvider node = new NodeProvider()
				.url("https://marine.fr/noeuds");

		when(integrationRequestCustomDao.findByUUIDAndLock(integrationRequest.getUuid())).thenReturn(integrationRequest);
		when(providerHelper.getNodeProviderByUUID(integrationRequest.getNodeProviderId())).thenReturn(node);
		doNothing().when(kalimProviderHelper).sendReport(eq(node.getUrl()), reportCaptor.capture());

		integrationRequestService.handleIntegrationRequest(integrationRequest.getUuid());

		assertThat(integrationRequest.getSendRequestDate()).isNotNull();
		assertThat(integrationRequest).hasFieldOrPropertyWithValue("progressStatus", ProgressStatus.REPORT_SUCCESS);

		final Report report = reportCaptor.getValue();
		assertThat(report)
				.hasFieldOrPropertyWithValue("reportId", integrationRequest.getUuid())
				.hasFieldOrPropertyWithValue("resourceId", integrationRequest.getGlobalId())
				.hasFieldOrPropertyWithValue("treatmentDate", integrationRequest.getTreatmentDate())
				.hasFieldOrPropertyWithValue("method", integrationRequest.getMethod())
				.hasFieldOrPropertyWithValue("resourceTitle", integrationRequest.getResourceTitle())
				.hasFieldOrPropertyWithValue("version", integrationRequest.getVersion())
				.hasFieldOrPropertyWithValue("integrationStatus", integrationRequest.getIntegrationStatus())
				.hasFieldOrPropertyWithValue("integrationErrors", Collections.emptyList())
				.hasFieldOrPropertyWithValue("submissionDate", integrationRequest.getSubmissionDate())
				.hasFieldOrPropertyWithValue("submittedByHarvesting", integrationRequest.isSubmittedByHarvesting())
		;
		assertThat(report.getComment()).startsWith(expectedMessageBeginning);

		verify(integrationRequestDao).save(integrationRequest);
	}

}
