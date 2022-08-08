package org.rudi.microservice.kalim.service.scheduler.harvesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.VisibleForTesting;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.integration.IntegrationRequestService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.rudi.microservice.kalim.service.scheduler.harvesting.HarvestingConfiguration.OFFSET;
import static org.rudi.microservice.kalim.service.scheduler.harvesting.HarvestingConfiguration.UPDATED_AFTER;

@Service
@RequiredArgsConstructor
@Slf4j
class HarvestingHelper {

	private final HarvestingConfiguration configuration;
	private final DatasetService datasetService;
	private final IntegrationRequestService integrationRequestService;

	public Flux<IntegrationRequest> harvest(NodeProvider node) {
		return getMetadataList(node)
				.flux()
				.flatMap(Flux::fromIterable)
				.handle((metadata, sink) -> {
					try {
						sink.next(handleMetadata(metadata, node));
					} catch (DataverseAPIException | IllegalAccessException | IntegrationException e) {
						// TODO RUDI-942 il faut prévenir le noeud que cette metadata n'a pas pu être intégrée
						log.error("Integration request failed for metadata {}", metadata.getGlobalId(), e);
					}
				});
	}

	@VisibleForTesting
	Mono<List<Metadata>> getMetadataList(NodeProvider node) {
		final AtomicInteger count = new AtomicInteger(0);

		// Source : https://stackoverflow.com/a/53370449
		return fetchMetadataList(node, count.get())
				.expand(metadataList -> {
					final int currentCount = count.addAndGet(metadataList.getItems().size());
					if (metadataList.getTotal() > currentCount) {
						return fetchMetadataList(node, currentCount);
					} else {
						return Mono.empty();
					}
				})
				.flatMap(metadataList -> Flux.fromIterable(metadataList.getItems()))
				.collectList();
	}

	@Nonnull
	private Mono<MetadataList> fetchMetadataList(NodeProvider node, int offset) {
		final WebClient webClient = WebClient.builder()
				.baseUrl(node.getUrl())
				.build();
		return webClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(configuration.getResourcesPath())
						.queryParam(UPDATED_AFTER, node.getLastHarvestingDate())
						.queryParam(OFFSET, offset)
						.build())
				.exchange()
				.flatMap(response -> response.bodyToMono(MetadataList.class))
				.map(this::metadataListWithoutNullFields);
	}

	private MetadataList metadataListWithoutNullFields(MetadataList metadataList) {
		if (metadataList.getTotal() == null || metadataList.getItems() == null) {
			return metadataList.total(0L).items(Collections.emptyList());
		} else {
			return metadataList;
		}
	}

	private IntegrationRequest handleMetadata(Metadata metadata, NodeProvider node) throws DataverseAPIException, IllegalAccessException, IntegrationException {
		final Method method = getMethodToUse(metadata);
		return integrationRequestService.createIntegrationRequestFromHarvesting(metadata, method, node);
	}

	private Method getMethodToUse(Metadata metadata) throws DataverseAPIException {
		if (alreadyExists(metadata)) {
			final @Nullable OffsetDateTime deletedDateTime = metadata.getDatasetDates().getDeleted();
			if (deletedDateTime == null || deletedDateTime.isAfter(OffsetDateTime.now())) {
				return Method.PUT;
			} else {
				return Method.DELETE;
			}
		} else {
			return Method.POST;
		}
	}
	
	private boolean alreadyExists(Metadata metadata) throws DataverseAPIException {
		try {
			datasetService.getDataset(metadata.getGlobalId());
			return true;
		} catch (DatasetNotFoundException e) {
			return false;
		}
	}
}
