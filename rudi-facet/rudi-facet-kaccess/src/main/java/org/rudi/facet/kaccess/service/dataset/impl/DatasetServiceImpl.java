package org.rudi.facet.kaccess.service.dataset.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.api.search.mapper.MetadatafieldsMapper;
import org.rudi.facet.dataverse.bean.Dataset;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.bean.DatasetVersion;
import org.rudi.facet.dataverse.bean.Identifier;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kaccess.bean.DatasetSearchCriteria;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataList;
import org.rudi.facet.kaccess.bean.MetadataListFacets;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.MetadataBlockHelper;
import org.rudi.facet.kaccess.helper.search.mapper.SearchConfiguration;
import org.rudi.facet.kaccess.helper.search.mapper.SearchCriteriaMapper;
import org.rudi.facet.kaccess.helper.search.mapper.SearchElementDatasetMapper;
import org.rudi.facet.kaccess.helper.search.mapper.SearchElementDatasetMapperWithGetDataset;
import org.rudi.facet.kaccess.helper.search.mapper.SearchElementDatasetMapperWithMetadataBlocks;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.RUDI_ELEMENT_SPEC;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasetServiceImpl implements DatasetService {

	private static final List<FieldSpec> METADATAFIELDS = Arrays.asList(
			RudiMetadataField.STORAGE_STATUS, // actuellement obligatoire pour les mappers
			RudiMetadataField.GLOBAL_ID,
			RudiMetadataField.RESOURCE_TITLE,
			RudiMetadataField.SYNOPSIS,
			RudiMetadataField.SUMMARY_TEXT,
			RudiMetadataField.PRODUCER_ORGANIZATION_ID,
			RudiMetadataField.THEME,
			RudiMetadataField.KEYWORDS,
			RudiMetadataField.CONFIDENTIALITY
	);
	public static final String MISSING_GLOBAL_ID = "L'identifiant du jeu de donnée est absent";
	private final DatasetOperationAPI datasetOperationAPI;
	private final MetadataBlockHelper metadataBLockHelper;
	/**
	 * @see SearchConfiguration#searchElementDatasetMapperForMultipleDatasets(SearchElementDatasetMapperWithGetDataset, SearchElementDatasetMapperWithMetadataBlocks)
	 */
	private final SearchElementDatasetMapper searchElementDatasetMapperForMultipleDatasets;
	/**
	 * @see SearchConfiguration#searchElementDatasetMapperForOneDataset(org.rudi.facet.kaccess.helper.search.mapper.SearchElementDatasetMapperWithGetDataset)
	 */
	private final SearchElementDatasetMapper searchElementDatasetMapperForOneDataset;
	private final SearchCriteriaMapper searchCriteriaMapper;
	private final MetadatafieldsMapper metadatafieldsMapper;
	@Value("${dataverse.api.rudi.data.alias}")
	private String rudiAlias;
	@Value("${dataverse.api.rudi.archive.alias}")
	private String archiveAlias;
	@Value("${dataverse.api.useMockedMetadataBlocks:false}")
	private boolean useMockedMetadataBlocks;

	@Override
	public Metadata getDataset(String doi) throws DataverseAPIException {
		Dataset dataset = datasetOperationAPI.getDataset(doi);
		return metadataBLockHelper.datasetMetadataBlockToMetadata(dataset.getLatestVersion().getMetadataBlocks(),
				dataset.getLatestVersion().getDatasetPersistentId());
	}

	@Override
	@Nonnull
	public Metadata getDataset(UUID globalId) throws DataverseAPIException {

		if (globalId == null) {
			throw new DataverseAPIException(MISSING_GLOBAL_ID);
		}

		MetadataListFacets metadataListFacets = searchDatasets(
				new DatasetSearchCriteria().globalId(globalId).offset(0).limit(1), Collections.emptyList(), true);
		MetadataList metadataList = metadataListFacets.getMetadataList();

		if (metadataList.getTotal() == 0 || metadataList.getItems().isEmpty()) {
			throw DatasetNotFoundException.fromGlobalId(globalId);
		}

		return metadataList.getItems().get(0);
	}

	@Override
	public String createDataset(Metadata metadata) throws DataverseAPIException {

		// Création des metadatablocks
		DatasetVersion datasetVersion = new DatasetVersion()
				.metadataBlocks(metadataBLockHelper.metadataToDatasetMetadataBlock(metadata)).files(new ArrayList<>());

		Dataset dataset = new Dataset().datasetVersion(datasetVersion);
		Identifier identifier = datasetOperationAPI.createDataset(dataset, rudiAlias);

		return identifier.getPersistentId();
	}

	@Override
	public Metadata updateDataset(Metadata metadata) throws DataverseAPIException {
		// Création des metadatablocks
		DatasetVersion datasetVersion = new DatasetVersion()
				.metadataBlocks(metadataBLockHelper.metadataToDatasetMetadataBlock(metadata));

		DatasetVersion resultDatasetVersion = datasetOperationAPI.updateDataset(datasetVersion,
				metadata.getDataverseDoi());
		return metadataBLockHelper.datasetMetadataBlockToMetadata(resultDatasetVersion.getMetadataBlocks(),
				resultDatasetVersion.getDatasetPersistentId());
	}

	@Override
	public String archiveDataset(String doi) throws DataverseAPIException {
		datasetOperationAPI.moveDataset(doi, archiveAlias);
		return doi;
	}

	@Override
	public MetadataListFacets searchDatasets(DatasetSearchCriteria datasetSearchCriteria, List<String> facets)
			throws DataverseAPIException {

		return searchDatasets(datasetSearchCriteria, facets, false);
	}

	private MetadataListFacets searchDatasets(DatasetSearchCriteria datasetSearchCriteria, List<String> facets, boolean doNotUseMetadatafields) throws DataverseAPIException {
		final SearchParams rawSearchParams = searchCriteriaMapper.datasetSearchCriteriaToSearchParams(datasetSearchCriteria,
				CollectionUtils.isNotEmpty(facets));

		final SearchParams searchParams;
		if (doNotUseMetadatafields) {
			searchParams = rawSearchParams;
		} else {
			searchParams = rawSearchParams.toBuilder().metadatafields(getMetadatafieldsList()).build();
		}
		final SearchElements<SearchDatasetInfo> searchElements = datasetOperationAPI.searchDataset(searchParams);
		if (doNotUseMetadatafields) {
			return searchElementDatasetMapperForOneDataset.toMetadataListFacets(searchElements, facets);
		} else {
			if (useMockedMetadataBlocks) {
				injectMockedMetadataBlocks(searchElements);
			}
			return searchElementDatasetMapperForMultipleDatasets.toMetadataListFacets(searchElements, facets);
		}
	}

	private Set<String> getMetadatafieldsList() {
		return metadatafieldsMapper.map(RUDI_ELEMENT_SPEC, METADATAFIELDS);
	}

	private void injectMockedMetadataBlocks(SearchElements<SearchDatasetInfo> searchElements) {
		final DatasetMetadataBlock mockedDatasetMetadataBlocks = loadMockedDatasetMetadataBlocks();
		searchElements.getItems().forEach(searchDatasetInfo ->
				searchDatasetInfo.setMetadataBlocks(mockedDatasetMetadataBlocks));
	}

	private DatasetMetadataBlock loadMockedDatasetMetadataBlocks() {
		try {
			return new JsonResourceReader().read("mockedMetadataBlocks.json", DatasetMetadataBlock.class);
		} catch (IOException e) {
			log.error("Cannot read mockedMetadataBlocks.json", e);
			return new DatasetMetadataBlock();
		}
	}

	@Override
	public void deleteDataset(String doi) throws DataverseAPIException {
		datasetOperationAPI.deleteDataset(doi);
		log.info("Dataset " + doi + " successfully deleted.");
	}

	@Override
	public boolean datasetExists(DatasetSearchCriteria datasetSearchCriteria) throws DataverseAPIException {
		final DatasetSearchCriteria limitedSearchCriteria = datasetSearchCriteria
				.offset(0)
				.limit(1);
		final SearchParams searchParams = searchCriteriaMapper.datasetSearchCriteriaToSearchParams(
				limitedSearchCriteria,
				false);
		final SearchElements<SearchDatasetInfo> searchElements = datasetOperationAPI.searchDataset(searchParams);
		return searchElements.getTotal() > 0;
	}

	@Override
	public void deleteDataset(UUID globalId) throws DataverseAPIException {
		final DatasetSearchCriteria datasetSearchCriteria = new DatasetSearchCriteria().globalId(globalId);
		final SearchParams searchParams = searchCriteriaMapper.datasetSearchCriteriaToSearchParams(datasetSearchCriteria, false);

		final SearchElements<SearchDatasetInfo> searchElements = datasetOperationAPI.searchDataset(searchParams);

		final long exceptionsCount = searchElements.getItems().stream()
				.map(SearchDatasetInfo::getGlobalId)
				.map(doi -> {
					try {
						deleteDataset(doi);
						return null;
					} catch (DataverseAPIException | RuntimeException e) {
						log.info("Error when trying to delete dataset " + doi, e);
						return e;
					}
				})
				.filter(Objects::nonNull)
				.count();

		if (exceptionsCount > 0) {
			final String message = String.format("%s error(s) when deleting dataset(s) with globalId = %s", exceptionsCount, globalId);
			throw new DataverseAPIException(message);
		}
	}

	@Override
	public boolean datasetExists(UUID globalId) throws DataverseAPIException {

		if (globalId == null) {
			throw new DataverseAPIException(MISSING_GLOBAL_ID);
		}

		return datasetExists(new DatasetSearchCriteria().globalId(globalId));
	}

	@Override
	public boolean datasetExists(String doi) throws DataverseAPIException {

		if (doi == null) {
			throw new DataverseAPIException(MISSING_GLOBAL_ID);
		}

		try {
			datasetOperationAPI.getDataset(doi);
			return true;
		} catch (final DatasetNotFoundException e) {
			return false;
		}
	}
}
