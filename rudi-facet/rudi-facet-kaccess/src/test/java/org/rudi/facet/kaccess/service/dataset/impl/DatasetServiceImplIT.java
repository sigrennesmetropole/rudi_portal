package org.rudi.facet.kaccess.service.dataset.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kaccess.helper.search.mapper.SearchCriteriaMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatasetServiceImplIT {
	@InjectMocks
	private DatasetServiceImpl datasetService;
	@Mock
	private SearchCriteriaMapper searchCriteriaMapper;
	@Mock
	private DatasetOperationAPI datasetOperationAPI;

	@Test
	void deleteDataset_ok() throws DataverseAPIException {
		final UUID globalId = UUID.randomUUID();

		final String doi1 = "doi:10.5072/FK2/OFKEB1";
		final SearchDatasetInfo datasetWithSameGlobalId1 = new SearchDatasetInfo()
				.globalId(doi1);

		final String doi2 = "doi:10.5072/FK2/OFKEB2";
		final SearchDatasetInfo datasetWithSameGlobalId2 = new SearchDatasetInfo()
				.globalId(doi2);

		final SearchParams searchParamsWithGlobalId = SearchParams.builder()
				.q("*")
				.build();

		final SearchElements<SearchDatasetInfo> elementsWithSameGlobalId = new SearchElements<SearchDatasetInfo>()
				.addItemsItem(datasetWithSameGlobalId1)
				.addItemsItem(datasetWithSameGlobalId2);

		when(searchCriteriaMapper.datasetSearchCriteriaToSearchParams(any(), eq(false))).thenReturn(searchParamsWithGlobalId);
		when(datasetOperationAPI.searchDataset(searchParamsWithGlobalId)).thenReturn(elementsWithSameGlobalId);

		datasetService.deleteDataset(globalId);

		verify(datasetOperationAPI).deleteDataset(datasetWithSameGlobalId1.getGlobalId());
		verify(datasetOperationAPI).deleteDataset(datasetWithSameGlobalId2.getGlobalId());
	}

	@Test
	void deleteDataset_exceptions() throws DataverseAPIException {
		final UUID globalId = UUID.fromString("bcb7928e-306e-46c3-81cc-c6aee73c385c");

		final String doi1 = "doi:10.5072/FK2/OFKEB1";
		final SearchDatasetInfo datasetWithSameGlobalId1 = new SearchDatasetInfo()
				.globalId(doi1);

		final String doi2 = "doi:10.5072/FK2/OFKEB2";
		final SearchDatasetInfo datasetWithSameGlobalId2 = new SearchDatasetInfo()
				.globalId(doi2);

		final SearchParams searchParamsWithGlobalId = SearchParams.builder()
				.q("*")
				.build();

		final SearchElements<SearchDatasetInfo> elementsWithSameGlobalId = new SearchElements<SearchDatasetInfo>()
				.addItemsItem(datasetWithSameGlobalId1)
				.addItemsItem(datasetWithSameGlobalId2);

		when(searchCriteriaMapper.datasetSearchCriteriaToSearchParams(any(), eq(false))).thenReturn(searchParamsWithGlobalId);
		when(datasetOperationAPI.searchDataset(searchParamsWithGlobalId)).thenReturn(elementsWithSameGlobalId);
		doThrow(new DataverseAPIException("Cannot delete first Dataset")).when(datasetOperationAPI).deleteDataset(doi1);
		doThrow(new DataverseAPIException("Cannot delete second Dataset")).when(datasetOperationAPI).deleteDataset(doi2);

		assertThatThrownBy(() -> datasetService.deleteDataset(globalId))
				.isInstanceOf(DataverseAPIException.class)
				.hasMessage("2 error(s) when deleting dataset(s) with globalId = bcb7928e-306e-46c3-81cc-c6aee73c385c");
	}
}
