package org.rudi.microservice.kalim.service.integration;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.bean.ProgressStatus;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.KalimSpringBootTest;
import org.rudi.microservice.kalim.service.helper.provider.KalimProviderHelper;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PostIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.service.integration.impl.handlers.PutIntegrationRequestTreatmentHandler;
import org.rudi.microservice.kalim.storage.dao.integration.IntegrationRequestDao;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.rudi.microservice.kalim.service.KalimTestConfigurer.initMetadata;

/**
 * Class de test du service IntegrationRequest
 */
@KalimSpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationRequestServiceTest {

	private static Metadata metadata;
	private static Provider provider;
	private static NodeProvider nodeProvider;

	@MockBean
	private KalimProviderHelper mockedKalimProviderHelper;

	@MockBean
	private ProviderHelper providerHelper;

	@MockBean
	private OrganizationHelper organizationHelper;

    @MockBean
	private DatasetService datasetService;

	@Autowired
	private IntegrationRequestDao integrationRequestdao;

	@Autowired
	private IntegrationRequestService integrationRequestService;

	@Autowired
	private IntegrationRequestDao integrationRequestDao;

	@Autowired
	private PostIntegrationRequestTreatmentHandler postHandler;

	@Autowired
	private PutIntegrationRequestTreatmentHandler putHandler;

	@BeforeAll
	static void beforeAll() throws IOException {
		metadata = initMetadata();
		provider = initProvider();
		nodeProvider = initNodeProvider();
		setMetadataInfoProvider(nodeProvider, metadata);
	}

	private static void setMetadataInfoProvider(NodeProvider nodeProvider, Metadata metadata) {
		final var metadataProvider = metadata.getMetadataInfo().getMetadataProvider();
		metadataProvider.setOrganizationId(nodeProvider.getUuid());
	}

	private static NodeProvider initNodeProvider() {
		NodeProvider nodeProvider = new NodeProvider();
		nodeProvider.setUuid(UUID.randomUUID());
		nodeProvider.setVersion("v4.11.0.0");
		nodeProvider.setUrl("https://demo.dataverse.org");
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime openingDate = LocalDateTime.of(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), 0,
				0);
		nodeProvider.setOpeningDate(openingDate);
		return nodeProvider;
	}

	private static Provider initProvider() {
		return new Provider().uuid(UUID.randomUUID()).code(RandomStringUtils.randomAlphabetic(10));
	}

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(postHandler, "metadataValidators", Collections.emptyList());
		ReflectionTestUtils.setField(putHandler, "metadataValidators", Collections.emptyList());

		when(providerHelper.getProviderByNodeProviderUUID(Mockito.any())).thenReturn(provider);
		when(mockedKalimProviderHelper.getAuthenticatedNodeProvider()).thenReturn(nodeProvider);
		when(providerHelper.getNodeProviderByUUID(Mockito.any())).thenReturn(nodeProvider);
	}

	@AfterEach
	void tearDown() {
		integrationRequestDao.deleteAll();
	}

	@Test
	@Order(1)
	@Disabled("Contrôle désactivé par la RUDI-1459")
	void testCreateIntegrationRequestFromAnotherProvider() throws IntegrationException, IllegalAccessException, DataverseAPIException {

		final NodeProvider anotherNodeProvider = initNodeProvider();
		when(mockedKalimProviderHelper.getAuthenticatedNodeProvider()).thenReturn(anotherNodeProvider);

		IntegrationRequest integrationRequestResult = integrationRequestService.createIntegrationRequest(metadata,
				Method.POST);
		Assertions.assertNotNull(integrationRequestResult);

		when(datasetService.createDataset(Mockito.any())).thenReturn(UUID.randomUUID().toString());
		when(datasetService.getDataset((String) Mockito.any())).thenReturn(metadata);

		integrationRequestService.handleIntegrationRequest(integrationRequestResult.getUuid());
		IntegrationRequestEntity integrationRequest = integrationRequestdao.findByUuid(integrationRequestResult.getUuid());
		Assertions.assertEquals(ProgressStatus.INTEGRATION_HANDLED, integrationRequest.getProgressStatus());
		Assertions.assertEquals(IntegrationStatus.KO, integrationRequest.getIntegrationStatus());

	}

	@Test
	@Order(2)
	void testCreateIntegrationRequest() throws IntegrationException, IllegalAccessException, DataverseAPIException {

		IntegrationRequest integrationRequestResult = integrationRequestService.createIntegrationRequest(metadata,
				Method.POST);
		Assertions.assertNotNull(integrationRequestResult);

		when(datasetService.createDataset(Mockito.any())).thenReturn(UUID.randomUUID().toString());
		when(datasetService.getDataset((String) Mockito.any())).thenReturn(metadata);

		integrationRequestService.handleIntegrationRequest(integrationRequestResult.getUuid());
		IntegrationRequestEntity integrationRequest = integrationRequestdao.findByUuid(integrationRequestResult.getUuid());
		Assertions.assertEquals(ProgressStatus.INTEGRATION_HANDLED, integrationRequest.getProgressStatus());
		Assertions.assertEquals(IntegrationStatus.OK, integrationRequest.getIntegrationStatus());

	}

	@Test
	@Order(3)
	@Disabled("Contrôle désactivé par la RUDI-1459")
	void testUpdateIntegrationRequestFromAnotherProvider() throws IntegrationException, IllegalAccessException, DataverseAPIException {

		final NodeProvider anotherNodeProvider = initNodeProvider();
		when(mockedKalimProviderHelper.getAuthenticatedNodeProvider()).thenReturn(anotherNodeProvider);


		for (Media media : metadata.getAvailableFormats()) {
			media.getConnector().setUrl(media.getConnector().getUrl() + "/test");
		}
		IntegrationRequest integrationRequestResult = integrationRequestService.createIntegrationRequest(metadata, Method.PUT);
		Assertions.assertNotNull(integrationRequestResult);

		when(datasetService.datasetExists((UUID) Mockito.any())).thenReturn(true);
		when(datasetService.getDataset((UUID) Mockito.any())).thenReturn(metadata);
		when(datasetService.updateDataset(Mockito.any())).thenReturn(metadata);

		integrationRequestService.handleIntegrationRequest(integrationRequestResult.getUuid());
		IntegrationRequestEntity integrationRequest = integrationRequestdao.findByUuid(integrationRequestResult.getUuid());
		Assertions.assertEquals(ProgressStatus.INTEGRATION_HANDLED, integrationRequest.getProgressStatus());
		Assertions.assertEquals(IntegrationStatus.KO, integrationRequest.getIntegrationStatus());
	}

	@Test
	@Order(4)
	@Disabled("Contrôle désactivé par la RUDI-1459")
	void testUpdateIntegrationRequestFromAnotherProviderThanTheCreator() throws IntegrationException, IllegalAccessException, DataverseAPIException, IOException {

		final NodeProvider anotherNodeProvider = initNodeProvider();
		final Metadata metadataToUpdate = initMetadata();
		setMetadataInfoProvider(anotherNodeProvider, metadataToUpdate);
		when(mockedKalimProviderHelper.getAuthenticatedNodeProvider()).thenReturn(anotherNodeProvider);

		for (Media media : metadata.getAvailableFormats()) {
			media.getConnector().setUrl(media.getConnector().getUrl() + "/test");
		}
		IntegrationRequest integrationRequestResult = integrationRequestService.createIntegrationRequest(metadata, Method.PUT);
		Assertions.assertNotNull(integrationRequestResult);

		when(datasetService.datasetExists(metadata.getGlobalId())).thenReturn(true);
		when(datasetService.getDataset(metadata.getGlobalId())).thenReturn(metadata);
		when(datasetService.updateDataset(Mockito.any())).thenReturn(metadata);

		integrationRequestService.handleIntegrationRequest(integrationRequestResult.getUuid());
		IntegrationRequestEntity integrationRequest = integrationRequestdao.findByUuid(integrationRequestResult.getUuid());
		Assertions.assertEquals(ProgressStatus.INTEGRATION_HANDLED, integrationRequest.getProgressStatus());
		Assertions.assertEquals(IntegrationStatus.KO, integrationRequest.getIntegrationStatus());
	}

	@Test
	@Order(5)
	void testUpdateIntegrationRequest() throws IntegrationException, IllegalAccessException, DataverseAPIException {

		for (Media media : metadata.getAvailableFormats()) {
			media.getConnector().setUrl(media.getConnector().getUrl() + "/test");
		}
		IntegrationRequest integrationRequestResult = integrationRequestService.createIntegrationRequest(metadata, Method.PUT);
		Assertions.assertNotNull(integrationRequestResult);

		when(datasetService.datasetExists((UUID) Mockito.any())).thenReturn(true);
		when(datasetService.getDataset((UUID) Mockito.any())).thenReturn(metadata);
		when(datasetService.updateDataset(Mockito.any())).thenReturn(metadata);

		integrationRequestService.handleIntegrationRequest(integrationRequestResult.getUuid());
		IntegrationRequestEntity integrationRequest = integrationRequestdao.findByUuid(integrationRequestResult.getUuid());
		Assertions.assertEquals(ProgressStatus.INTEGRATION_HANDLED, integrationRequest.getProgressStatus());
		Assertions.assertEquals(IntegrationStatus.OK, integrationRequest.getIntegrationStatus());
	}

	@Test
	@Order(6)
	@Disabled("Contrôle désactivé par la RUDI-1459")
	void testDeleteIntegrationRequestFromAnotherProvider() throws IntegrationException, IllegalAccessException, DataverseAPIException {

		final NodeProvider anotherNodeProvider = initNodeProvider();
		when(mockedKalimProviderHelper.getAuthenticatedNodeProvider()).thenReturn(anotherNodeProvider);

		IntegrationRequest integrationRequestResult = integrationRequestService.createIntegrationRequest(metadata, Method.DELETE);
		Assertions.assertNotNull(integrationRequestResult);

		when(datasetService.getDataset((UUID) Mockito.any())).thenReturn(metadata);
		when(datasetService.archiveDataset(Mockito.any())).thenReturn("");

		integrationRequestService.handleIntegrationRequest(integrationRequestResult.getUuid());
		IntegrationRequestEntity integrationRequest = integrationRequestdao.findByUuid(integrationRequestResult.getUuid());
		Assertions.assertEquals(ProgressStatus.INTEGRATION_HANDLED, integrationRequest.getProgressStatus());
		Assertions.assertEquals(IntegrationStatus.KO, integrationRequest.getIntegrationStatus());
	}

	@Test
	@Order(7)
	void testDeleteIntegrationRequest() throws IntegrationException, IllegalAccessException, DataverseAPIException {

		IntegrationRequest integrationRequestResult = integrationRequestService.createIntegrationRequest(metadata, Method.DELETE);
		Assertions.assertNotNull(integrationRequestResult);

		when(datasetService.getDataset((UUID) Mockito.any())).thenReturn(metadata);
		when(datasetService.archiveDataset(Mockito.any())).thenReturn("");

		integrationRequestService.handleIntegrationRequest(integrationRequestResult.getUuid());
		IntegrationRequestEntity integrationRequest = integrationRequestdao.findByUuid(integrationRequestResult.getUuid());
		Assertions.assertEquals(ProgressStatus.INTEGRATION_HANDLED, integrationRequest.getProgressStatus());
		Assertions.assertEquals(IntegrationStatus.OK, integrationRequest.getIntegrationStatus());
	}

}
