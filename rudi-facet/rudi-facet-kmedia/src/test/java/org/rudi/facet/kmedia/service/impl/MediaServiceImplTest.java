package org.rudi.facet.kmedia.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.dataset.FileOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.Dataset;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.bean.Identifier;
import org.rudi.facet.dataverse.model.search.SearchParams;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaDataset;
import org.rudi.facet.kmedia.bean.MediaDatasetList;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.rudi.facet.kmedia.bean.MediaSearchCriteria;
import org.rudi.facet.kmedia.helper.dataset.metadatablock.MediaDatasetBlockHelper;
import org.rudi.facet.kmedia.helper.search.mapper.MediaSearchCriteriaMapper;
import org.rudi.facet.kmedia.helper.search.mapper.MediaSearchElementMapper;

import java.io.File;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MediaServiceImplTest {
	@InjectMocks
	private MediaServiceImpl mediaService;
	@Mock
	private DatasetOperationAPI datasetOperationAPI;
	@Mock
	private FileOperationAPI fileOperationAPI;
	@Mock
	private MediaDatasetBlockHelper mediaDatasetBlockHelper;
	@Mock
	private MediaSearchCriteriaMapper mediaSearchCriteriaMapper;
	@Mock
	private MediaSearchElementMapper mediaSearchElementMapper;

	@Captor
	private ArgumentCaptor<MediaSearchCriteria> mediaSearchCriteriaCaptor;

	@Captor
	private ArgumentCaptor<Dataset> createdDatasetCaptor;
	@Captor
	private ArgumentCaptor<MediaDataset> createdMediaDatasetCaptor;

	@Test
	void setSingleMediaFileFor_creationDatasetFournisseur() throws DataverseAPIException {

		final UUID providerUuid = UUID.fromString("2cbd3aca-1240-56ba-cd5b-7a81398825f2");
		final File tempFile = new File("tempFile");
		final SearchParams searchParams = SearchParams.builder()
				.q("MOCK QUERY")
				.build();
		final MediaDatasetList emptyMediaDatasetList = new MediaDatasetList();
		final DatasetMetadataBlock datasetMetadataBlock = mock(DatasetMetadataBlock.class);
		final Identifier createdMediaDatasetIdentifier = new Identifier()
				.persistentId("doi:10.5072/FK2/1QOX4J");

		when(mediaSearchCriteriaMapper.mediaSearchCriteriaToSearchParams(mediaSearchCriteriaCaptor.capture())).thenReturn(searchParams);
		when(mediaSearchElementMapper.toMediaDatasetList(datasetOperationAPI.searchDataset(searchParams))).thenReturn(emptyMediaDatasetList);
		when(mediaDatasetBlockHelper.mediaDatasetToDatasetMetadataBlock(createdMediaDatasetCaptor.capture())).thenReturn(datasetMetadataBlock);
		when(datasetOperationAPI.createDataset(createdDatasetCaptor.capture(), any())).thenReturn(createdMediaDatasetIdentifier);

		mediaService.setMediaFor(MediaOrigin.PROVIDER, providerUuid, KindOfData.LOGO, tempFile);

		final MediaDataset createdMediaDataset = createdMediaDatasetCaptor.getValue();
		assertThat(createdMediaDataset)
				.hasFieldOrPropertyWithValue("kindOfData", KindOfData.LOGO)
				.hasFieldOrPropertyWithValue("authorAffiliation", MediaOrigin.PROVIDER)
				.hasFieldOrPropertyWithValue("authorIdentifier", providerUuid)
		;

		final Dataset createdDataset = createdDatasetCaptor.getValue();
		assertThat(createdDataset)
				.hasFieldOrPropertyWithValue("datasetVersion.metadataBlocks", datasetMetadataBlock)
				.hasFieldOrPropertyWithValue("datasetVersion.files", Collections.emptyList())
		;

		verify(fileOperationAPI).setSingleDatasetFile(createdMediaDatasetIdentifier.getPersistentId(), tempFile);
	}

	@Test
	void setSingleMediaFileFor_creationDatasetProducteur() throws DataverseAPIException {

		final UUID providerUuid = UUID.fromString("2cbd3aca-1240-56ba-cd5b-7a81398825f2");
		final File tempFile = new File("tempFile");
		final SearchParams searchParams = SearchParams.builder()
				.q("MOCK QUERY")
				.build();
		final MediaDatasetList emptyMediaDatasetList = new MediaDatasetList();
		final DatasetMetadataBlock datasetMetadataBlock = mock(DatasetMetadataBlock.class);
		final Identifier createdMediaDatasetIdentifier = new Identifier()
				.persistentId("doi:10.5072/FK2/1QOX4J");

		when(mediaSearchCriteriaMapper.mediaSearchCriteriaToSearchParams(mediaSearchCriteriaCaptor.capture())).thenReturn(searchParams);
		when(mediaSearchElementMapper.toMediaDatasetList(datasetOperationAPI.searchDataset(searchParams))).thenReturn(emptyMediaDatasetList);
		when(mediaDatasetBlockHelper.mediaDatasetToDatasetMetadataBlock(createdMediaDatasetCaptor.capture())).thenReturn(datasetMetadataBlock);
		when(datasetOperationAPI.createDataset(createdDatasetCaptor.capture(), any())).thenReturn(createdMediaDatasetIdentifier);

		mediaService.setMediaFor(MediaOrigin.PRODUCER, providerUuid, KindOfData.LOGO, tempFile);

		final MediaDataset createdMediaDataset = createdMediaDatasetCaptor.getValue();
		assertThat(createdMediaDataset)
				.hasFieldOrPropertyWithValue("kindOfData", KindOfData.LOGO)
				.hasFieldOrPropertyWithValue("authorAffiliation", MediaOrigin.PRODUCER)
				.hasFieldOrPropertyWithValue("authorIdentifier", providerUuid)
		;

		final Dataset createdDataset = createdDatasetCaptor.getValue();
		assertThat(createdDataset)
				.hasFieldOrPropertyWithValue("datasetVersion.metadataBlocks", datasetMetadataBlock)
				.hasFieldOrPropertyWithValue("datasetVersion.files", Collections.emptyList())
		;

		verify(fileOperationAPI).setSingleDatasetFile(createdMediaDatasetIdentifier.getPersistentId(), tempFile);
	}

	@Test
	void setSingleMediaFileFor_remplacement() throws DataverseAPIException {

		final UUID providerUuid = UUID.fromString("2cbd3aca-1240-56ba-cd5b-7a81398825f2");
		final File tempFile = new File("tempFile");
		final SearchParams searchParams = SearchParams.builder()
				.q("MOCK QUERY")
				.build();
		final MediaDataset existingMediaDataset = new MediaDataset().dataverseDoi("doi:10.5072/FK2/1QOX4J");
		final MediaDatasetList mediaDatasetList = new MediaDatasetList().addItemsItem(existingMediaDataset);
		final Identifier existingMediaDatasetIdentifier = new Identifier()
				.persistentId(existingMediaDataset.getDataverseDoi());

		when(mediaSearchCriteriaMapper.mediaSearchCriteriaToSearchParams(mediaSearchCriteriaCaptor.capture())).thenReturn(searchParams);
		when(mediaSearchElementMapper.toMediaDatasetList(datasetOperationAPI.searchDataset(searchParams))).thenReturn(mediaDatasetList);

		mediaService.setMediaFor(MediaOrigin.PROVIDER, providerUuid, KindOfData.LOGO, tempFile);

		verify(fileOperationAPI).setSingleDatasetFile(existingMediaDatasetIdentifier.getPersistentId(), tempFile);
	}
}
