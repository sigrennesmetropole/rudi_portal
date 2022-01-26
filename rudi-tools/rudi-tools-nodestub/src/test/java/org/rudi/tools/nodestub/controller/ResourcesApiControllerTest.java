package org.rudi.tools.nodestub.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.test.RudiAssertions;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.rudi.tools.nodestub.service.ResourcesService;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.rudi.tools.nodestub.controller.ResourcesApiController.DEFAULT_LIMIT;
import static org.rudi.tools.nodestub.controller.ResourcesApiController.DEFAULT_OFFSET;

@ExtendWith(MockitoExtension.class)
class ResourcesApiControllerTest {
	private static final JsonResourceReader JSON_RESOURCE_READER = new JsonResourceReader();
	private ResourcesApiController resourcesApiController;
	@Mock
	private NodeStubConfiguration nodeStubConfiguration;
	@Mock
	private ResourcesService resourcesService;

	@BeforeEach
	void setUp() {
		resourcesApiController = new ResourcesApiController(nodeStubConfiguration, JSON_RESOURCE_READER.getObjectMapper(), resourcesService);
	}

	@Test
	void getRessourcesNoParameters() {
		final Metadata expectedMetadata = new Metadata();
		final List<Metadata> metadataList = Collections.singletonList(expectedMetadata);

		when(resourcesService.getMetadataList(DEFAULT_LIMIT, DEFAULT_OFFSET, null)).thenReturn(metadataList);

		final ResponseEntity<MetadataList> ressourcesEntity = resourcesApiController.getRessources(null, null, null, null);

		RudiAssertions.assertThat(ressourcesEntity)
				.hasNoErrorStatus()
				.asItemsList()
				.isSameAs(metadataList);
	}

	@Test
	void getRessourcesLimitAndOffset() {
		final Metadata expectedMetadata = new Metadata();
		final List<Metadata> metadataList = Collections.singletonList(expectedMetadata);

		when(resourcesService.getMetadataList(2, 1, null)).thenReturn(metadataList);

		final ResponseEntity<MetadataList> ressourcesEntity = resourcesApiController.getRessources(2, 1, null, null);

		RudiAssertions.assertThat(ressourcesEntity)
				.hasNoErrorStatus()
				.asItemsList()
				.isSameAs(metadataList);
	}
}
