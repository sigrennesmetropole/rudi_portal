package org.rudi.microservice.konsult.service.metadata.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataFacetValues;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kaccess.helper.FacetsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class MetadataWithSameThemeFinder {
	private static final String PRODUCER_ORGANIZATION_NAME_FACET = RudiMetadataField.PRODUCER_ORGANIZATION_NAME.getFacet();
	private final DatasetService datasetService;
	private final FacetsHelper facetsHelper;

	/**
	 * Find datasets with the same theme. See details in RUDI-292.
	 */
	public List<Metadata> find(String doi, Integer limit) throws DataverseAPIException {
		final var baseDataset = datasetService.getDataset(doi);

		final var datasetsFromOtherProducers = searchDatasetsFromOtherProducers(baseDataset, limit);
		final var datasetsFromTheSameProducer = searchDatasetsFromTheSameProducer(baseDataset, limit - datasetsFromOtherProducers.size());

		return ListUtils.union(datasetsFromOtherProducers, datasetsFromTheSameProducer);
	}

	private List<Metadata> searchDatasetsFromOtherProducers(Metadata baseDataset, int limit) throws DataverseAPIException {
		final var otherProducerNames = getTopOtherProducerNames(baseDataset, limit);

		final List<Metadata> datasets = new ArrayList<>(limit);
		for (final var otherProducerName : otherProducerNames) {
			final var otherProducerDataset = searchDatasets(baseDataset, otherProducerName, 1);
			datasets.addAll(otherProducerDataset);
		}

		return datasets;
	}

	private List<String> getTopOtherProducerNames(Metadata baseDataset, int limit) throws DataverseAPIException {
		final var datasetSearchCriteria = new DatasetSearchCriteria()
				.themes(Collections.singletonList(baseDataset.getTheme()));
		final var metadataListFacetsWithSameTheme = datasetService.searchDatasets(datasetSearchCriteria, Collections.singletonList(PRODUCER_ORGANIZATION_NAME_FACET));

		final var baseDatasetProducerName = baseDataset.getProducer().getOrganizationName();
		final var otherProducerNames = facetsHelper.getValues(RudiMetadataField.PRODUCER_ORGANIZATION_NAME, metadataListFacetsWithSameTheme).stream()
				.filter(facetValue -> facetValue.getCount() >= 1)
				.filter(facetValue -> !facetValue.getValue().equals(baseDatasetProducerName))
				.sorted(Comparator.comparingInt(MetadataFacetValues::getCount))
				.map(MetadataFacetValues::getValue)
				.collect(Collectors.toList());
		return otherProducerNames.subList(0, Math.min(limit, otherProducerNames.size()));
	}

	private List<Metadata> searchDatasetsFromTheSameProducer(Metadata baseDataset, int remainingLimit) throws DataverseAPIException {
		if (remainingLimit > 0) {
			final var datasetsFromTheSameProducer = searchDatasets(baseDataset, baseDataset.getProducer().getOrganizationName(), remainingLimit + 1).stream()
					.filter(dataset -> !dataset.getDataverseDoi().equals(baseDataset.getDataverseDoi())) // exclude base dataset (the + 1 in "remainingLimit + 1")
					.collect(Collectors.toList());
			return datasetsFromTheSameProducer.subList(0, Math.min(remainingLimit, datasetsFromTheSameProducer.size()));
		} else {
			return Collections.emptyList();
		}
	}

	private List<Metadata> searchDatasets(Metadata baseDataset, String producerName, int limit) throws DataverseAPIException {
		final var otherProducerDatasetSearchCriteria = new DatasetSearchCriteria()
				.themes(Collections.singletonList(baseDataset.getTheme()))
				.producerNames(Collections.singletonList(producerName))
				.keywords(baseDataset.getKeywords())
				.orderByScoreOfKeywords(true)
				.limit(limit);
		final var searchResult = datasetService.searchDatasets(otherProducerDatasetSearchCriteria, Collections.emptyList());
		return searchResult.getMetadataList().getItems();
	}

	public Integer getNumberOfDatasetsOnTheSameTheme(String doi) throws DataverseAPIException {
		final var baseDataset = datasetService.getDataset(doi);
		final var otherProducerDatasetSearchCriteria = new DatasetSearchCriteria()
				.themes(Collections.singletonList(baseDataset.getTheme()))
				.keywords(baseDataset.getKeywords())
				.orderByScoreOfKeywords(true)
				.limit(1);
		final var searchResult = datasetService.searchDatasets(otherProducerDatasetSearchCriteria, Collections.emptyList());
		return searchResult.getMetadataList().getTotal().intValue();
	}
}
