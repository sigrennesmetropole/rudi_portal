package org.rudi.tools.nodestub.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.rudi.tools.nodestub.controller.ResourcesApiController.DEFAULT_LIMIT;
import static org.rudi.tools.nodestub.controller.ResourcesApiController.DEFAULT_OFFSET;

@ExtendWith(MockitoExtension.class)
class ResourcesServiceImplTest {
	public static final File RESOURCES_DIRECTORY = new File("src/test/resources/resources");
	public static final File REPORTS_DIRECTORY = new File("src/test/resources/reports");
	private static final JsonResourceReader JSON_RESOURCE_READER = new JsonResourceReader();
	public static final String METADATA_PATH = "resources/f382ec4f-034b-411d-aab7-5b97a15eb92e.json";
	private ResourcesServiceImpl resourcesService;
	@Mock
	private NodeStubConfiguration nodeStubConfiguration;

	@BeforeEach
	void setUp() {
		resourcesService = new ResourcesServiceImpl(nodeStubConfiguration, JSON_RESOURCE_READER.getObjectMapper());
	}

	@Test
	void getMetadataListNoParameters() throws IOException {
		final Metadata expectedMetadata = JSON_RESOURCE_READER.read(METADATA_PATH, Metadata.class);

		when(nodeStubConfiguration.getResourcesDirectory()).thenReturn(RESOURCES_DIRECTORY);

		final List<Metadata> metadataList = resourcesService.getMetadataList(DEFAULT_LIMIT, DEFAULT_OFFSET, null);

		assertThat(metadataList)
				.contains(expectedMetadata);
	}

	@Test
	void getMetadataListUpdateAfter() throws IOException {
		final Metadata expectedMetadata = JSON_RESOURCE_READER.read(METADATA_PATH, Metadata.class);

		when(nodeStubConfiguration.getResourcesDirectory()).thenReturn(RESOURCES_DIRECTORY);

		final OffsetDateTime updateAfter = expectedMetadata.getDatasetDates().getUpdated().plus(1, ChronoUnit.SECONDS);
		final List<Metadata> metadataList = resourcesService.getMetadataList(DEFAULT_LIMIT, DEFAULT_OFFSET, updateAfter);

		assertThat(metadataList)
				.doesNotContain(expectedMetadata);
	}

	@Test
	void getMetadataListNonExistingDirectory() {
		final File emptyDirectory = new File("src/test/resources/non-existing-directory");

		when(nodeStubConfiguration.getResourcesDirectory()).thenReturn(emptyDirectory);

		final List<Metadata> metadataList = resourcesService.getMetadataList(DEFAULT_LIMIT, DEFAULT_OFFSET, null);

		assertThat(metadataList)
				.isEmpty();
	}

	@Test
	void getMetadataListLimit0() {
		when(nodeStubConfiguration.getResourcesDirectory()).thenReturn(RESOURCES_DIRECTORY);

		final List<Metadata> metadataList = resourcesService.getMetadataList(0, DEFAULT_LIMIT, null);

		assertThat(metadataList)
				.isEmpty();
	}

	@Test
	void getMetadataListInvalidFile() {
		when(nodeStubConfiguration.getResourcesDirectory()).thenReturn(REPORTS_DIRECTORY);

		final List<Metadata> metadataList = resourcesService.getMetadataList(DEFAULT_LIMIT, DEFAULT_OFFSET, null);

		assertThat(metadataList)
				.isEmpty();
	}

}
