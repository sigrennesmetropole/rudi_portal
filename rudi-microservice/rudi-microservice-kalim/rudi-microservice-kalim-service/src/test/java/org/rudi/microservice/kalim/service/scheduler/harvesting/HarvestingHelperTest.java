package org.rudi.microservice.kalim.service.scheduler.harvesting;

import com.fasterxml.jackson.core.JsonProcessingException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.test.RudiAssertions;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.bean.ReferenceDates;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.integration.IntegrationRequestService;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HarvestingHelperTest {
	private static final JsonResourceReader JSON_RESOURCE_READER = new JsonResourceReader();
	private static final String RESOURCES_PATH = "/resources";

	@InjectMocks
	private HarvestingHelper service;
	@Mock
	private HarvestingConfiguration configuration;
	private MockWebServer mockWebServer;
	@Mock
	private IntegrationRequestService integrationRequestService;
	@Mock
	private DatasetService datasetService;

	@BeforeEach
	void setUp() throws IOException {
		mockWebServer = new MockWebServer();
		mockWebServer.start();
	}

	@Test
	void getMetadataListEmptyResult() throws JsonProcessingException, InterruptedException {
		final NodeProvider node = new NodeProvider()
				.url("http://localhost:" + mockWebServer.getPort())
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 18, 16, 0, 0));
		final MetadataList singlePageList = new MetadataList();

		when(configuration.getResourcesPath()).thenReturn(RESOURCES_PATH);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(singlePageList))
				.addHeader("Content-Type", "application/json")
		);

		StepVerifier.create(service.getMetadataList(node))
				.assertNext(metadataList -> assertThat(metadataList).isEmpty())
				.verifyComplete();

		RudiAssertions.assertThat(mockWebServer.takeRequest()).hasQuery("updated_after=2021-08-18T16:00&offset=0");
	}

	@Test
	void getMetadataListSinglePage() throws JsonProcessingException, InterruptedException {
		final NodeProvider node = new NodeProvider()
				.url("http://localhost:" + mockWebServer.getPort())
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 18, 16, 0, 0));
		final Metadata singleMetadata = new Metadata();
		final MetadataList singlePageList = new MetadataList()
				.total(1L)
				.addItemsItem(singleMetadata);

		when(configuration.getResourcesPath()).thenReturn(RESOURCES_PATH);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(singlePageList))
				.addHeader("Content-Type", "application/json")
		);

		StepVerifier.create(service.getMetadataList(node))
				.assertNext(metadataList -> assertThat(metadataList).containsExactly(singleMetadata))
				.verifyComplete();

		RudiAssertions.assertThat(mockWebServer.takeRequest()).hasQuery("updated_after=2021-08-18T16:00&offset=0");
	}

	@Test
	void getMetadataListMultiplePages() throws JsonProcessingException, InterruptedException {
		final NodeProvider node = new NodeProvider()
				.url("http://localhost:" + mockWebServer.getPort())
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 18, 16, 0, 0));

		final long total = 5L;

		final Metadata page1Metadata1 = new Metadata().resourceTitle("page1Metadata1");
		final Metadata page1Metadata2 = new Metadata().resourceTitle("page1Metadata2");
		final MetadataList page1List = new MetadataList()
				.total(total)
				.addItemsItem(page1Metadata1)
				.addItemsItem(page1Metadata2);

		final Metadata page2Metadata1 = new Metadata().resourceTitle("page2Metadata1");
		final Metadata page2Metadata2 = new Metadata().resourceTitle("page2Metadata2");
		final MetadataList page2List = new MetadataList()
				.total(total)
				.addItemsItem(page2Metadata1)
				.addItemsItem(page2Metadata2);

		final Metadata page3Metadata = new Metadata().resourceTitle("page3Metadata");
		final MetadataList page3List = new MetadataList()
				.total(total)
				.addItemsItem(page3Metadata);

		when(configuration.getResourcesPath()).thenReturn(RESOURCES_PATH);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(page1List))
				.addHeader("Content-Type", "application/json")
		);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(page2List))
				.addHeader("Content-Type", "application/json")
		);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(page3List))
				.addHeader("Content-Type", "application/json")
		);

		StepVerifier.create(service.getMetadataList(node))
				.assertNext(metadataList -> assertThat(metadataList).containsExactly(page1Metadata1, page1Metadata2, page2Metadata1, page2Metadata2, page3Metadata))
				.verifyComplete();

		RudiAssertions.assertThat(mockWebServer.takeRequest()).hasQuery("updated_after=2021-08-18T16:00&offset=0");
		RudiAssertions.assertThat(mockWebServer.takeRequest()).hasQuery("updated_after=2021-08-18T16:00&offset=2");
		RudiAssertions.assertThat(mockWebServer.takeRequest()).hasQuery("updated_after=2021-08-18T16:00&offset=4");

	}

	@Test
	void harvestPostFirstOkSecondKo() throws JsonProcessingException, IntegrationException, IllegalAccessException, DataverseAPIException {
		final NodeProvider node = new NodeProvider()
				.url("http://localhost:" + mockWebServer.getPort())
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 18, 16, 0, 0));
		final Metadata metadataWithoutError = new Metadata().resourceTitle("metadataWithoutError").globalId(UUID.randomUUID());
		final Metadata metadataWithError = new Metadata().resourceTitle("metadataWithError").globalId(UUID.randomUUID());
		final MetadataList metadataList = new MetadataList()
				.total(2L)
				.addItemsItem(metadataWithoutError)
				.addItemsItem(metadataWithError);
		final IntegrationRequest successfulIntegrationRequest = new IntegrationRequest();

		when(configuration.getResourcesPath()).thenReturn(RESOURCES_PATH);
		when(datasetService.getDataset(metadataWithoutError.getGlobalId())).thenThrow(DatasetNotFoundException.fromGlobalId(metadataWithoutError.getGlobalId()));
		when(datasetService.getDataset(metadataWithError.getGlobalId())).thenThrow(DatasetNotFoundException.fromGlobalId(metadataWithError.getGlobalId()));
		when(integrationRequestService.createIntegrationRequestFromHarvesting(metadataWithoutError, Method.POST, node)).thenReturn(successfulIntegrationRequest);
		when(integrationRequestService.createIntegrationRequestFromHarvesting(metadataWithError, Method.POST, node)).thenThrow(new IntegrationException("Mocked exception"));

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(metadataList))
				.addHeader("Content-Type", "application/json")
		);

		StepVerifier.create(service.harvest(node))
				.assertNext(integrationRequest -> assertThat(integrationRequest).isSameAs(successfulIntegrationRequest))
				.verifyComplete();

	}

	@Test
	void harvestPut() throws JsonProcessingException, IntegrationException, IllegalAccessException, DataverseAPIException {
		final NodeProvider node = new NodeProvider()
				.url("http://localhost:" + mockWebServer.getPort())
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 18, 10, 0));
		final Metadata existingMetadata = new Metadata().globalId(UUID.randomUUID())
				.datasetDates(new ReferenceDates()
						.created(LocalDateTime.of(2021, Month.AUGUST, 17, 10, 0)));
		final Metadata modifiedMetadata = new Metadata().globalId(existingMetadata.getGlobalId())
				.datasetDates(new ReferenceDates()
						.created(LocalDateTime.of(2021, Month.AUGUST, 17, 10, 0))
						.updated(LocalDateTime.of(2021, Month.AUGUST, 19, 10, 0)));
		final MetadataList metadataList = new MetadataList()
				.total(1L)
				.addItemsItem(modifiedMetadata);
		final IntegrationRequest successfulIntegrationRequest = new IntegrationRequest();

		when(configuration.getResourcesPath()).thenReturn(RESOURCES_PATH);
		when(datasetService.getDataset(modifiedMetadata.getGlobalId())).thenReturn(existingMetadata);
		when(integrationRequestService.createIntegrationRequestFromHarvesting(modifiedMetadata, Method.PUT, node)).thenReturn(successfulIntegrationRequest);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(metadataList))
				.addHeader("Content-Type", "application/json")
		);

		StepVerifier.create(service.harvest(node))
				.assertNext(integrationRequest -> assertThat(integrationRequest).isSameAs(successfulIntegrationRequest))
				.verifyComplete();

	}

	@Test
	void harvestDelete() throws JsonProcessingException, IntegrationException, IllegalAccessException, DataverseAPIException {
		final NodeProvider node = new NodeProvider()
				.url("http://localhost:" + mockWebServer.getPort())
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 20, 10, 0));
		final Metadata existingMetadata = new Metadata().globalId(UUID.randomUUID())
				.datasetDates(new ReferenceDates()
						.created(LocalDateTime.of(2021, Month.AUGUST, 17, 10, 0))
						.updated(LocalDateTime.of(2021, Month.AUGUST, 19, 10, 0)));
		final Metadata metadataToDelete = new Metadata().globalId(existingMetadata.getGlobalId())
				.datasetDates(new ReferenceDates()
						.created(LocalDateTime.of(2021, Month.AUGUST, 17, 10, 0))
						.updated(LocalDateTime.of(2021, Month.AUGUST, 19, 10, 0))
						.deleted(LocalDateTime.now().minus(1, ChronoUnit.HOURS)));
		final MetadataList metadataList = new MetadataList()
				.total(1L)
				.addItemsItem(metadataToDelete);
		final IntegrationRequest successfulIntegrationRequest = new IntegrationRequest();

		when(configuration.getResourcesPath()).thenReturn(RESOURCES_PATH);
		when(datasetService.getDataset(metadataToDelete.getGlobalId())).thenReturn(existingMetadata);
		when(integrationRequestService.createIntegrationRequestFromHarvesting(metadataToDelete, Method.DELETE, node)).thenReturn(successfulIntegrationRequest);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(metadataList))
				.addHeader("Content-Type", "application/json")
		);

		StepVerifier.create(service.harvest(node))
				.assertNext(integrationRequest -> assertThat(integrationRequest).isSameAs(successfulIntegrationRequest))
				.verifyComplete();

	}

	@Test
	void harvestNotYetDeleted() throws JsonProcessingException, IntegrationException, IllegalAccessException, DataverseAPIException {
		final NodeProvider node = new NodeProvider()
				.url("http://localhost:" + mockWebServer.getPort())
				.lastHarvestingDate(LocalDateTime.of(2021, Month.AUGUST, 20, 10, 0));
		final Metadata existingMetadata = new Metadata().globalId(UUID.randomUUID())
				.datasetDates(new ReferenceDates()
						.created(LocalDateTime.of(2021, Month.AUGUST, 17, 10, 0))
						.updated(LocalDateTime.of(2021, Month.AUGUST, 19, 10, 0)));
		final Metadata metadataToDelete = new Metadata().globalId(existingMetadata.getGlobalId())
				.datasetDates(new ReferenceDates()
						.created(LocalDateTime.of(2021, Month.AUGUST, 17, 10, 0))
						.updated(LocalDateTime.of(2021, Month.AUGUST, 19, 10, 0))
						.deleted(LocalDateTime.now().plus(1, ChronoUnit.HOURS)));
		final MetadataList metadataList = new MetadataList()
				.total(1L)
				.addItemsItem(metadataToDelete);
		final IntegrationRequest successfulIntegrationRequest = new IntegrationRequest();

		when(configuration.getResourcesPath()).thenReturn(RESOURCES_PATH);
		when(datasetService.getDataset(metadataToDelete.getGlobalId())).thenReturn(existingMetadata);
		when(integrationRequestService.createIntegrationRequestFromHarvesting(metadataToDelete, Method.PUT, node)).thenReturn(successfulIntegrationRequest);

		mockWebServer.enqueue(new MockResponse()
				.setBody(JSON_RESOURCE_READER.getObjectMapper().writeValueAsString(metadataList))
				.addHeader("Content-Type", "application/json")
		);

		StepVerifier.create(service.harvest(node))
				.assertNext(integrationRequest -> assertThat(integrationRequest).isSameAs(successfulIntegrationRequest))
				.verifyComplete();

	}
}
