package org.rudi.facet.kmedia.helper.search.mapper;

import org.rudi.facet.dataverse.api.dataset.DatasetOperationAPI;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.dataverse.bean.Dataset;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.dataverse.model.search.SearchElements;
import org.rudi.facet.kmedia.bean.MediaDataset;
import org.rudi.facet.kmedia.bean.MediaDatasetList;
import org.rudi.facet.kmedia.helper.dataset.metadatablock.MediaDatasetBlockHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MediaSearchElementMapper {

	@Autowired
	private DatasetOperationAPI datasetOperationAPI;

	@Autowired
	private MediaDatasetBlockHelper mediaDatasetBlockHelper;

	public MediaDatasetList toMediaDatasetList(SearchElements<SearchDatasetInfo> searchElements)
			throws DataverseAPIException {
		List<MediaDataset> mediaDatasets = new ArrayList<>();

		if (searchElements == null) {
			return new MediaDatasetList().total(0L).items(Collections.emptyList());
		}

		for (SearchDatasetInfo searchDatasetInfo : searchElements.getItems()) {
			// SearchDatasetInfo ne contient pas tous les champs de MediaDataset
			// on doit donc recharger unitairement chaque dataset
			Dataset dataset = datasetOperationAPI.getDataset(searchDatasetInfo.getGlobalId());
			mediaDatasets.add(mediaDatasetBlockHelper.datasetMediaBlockToMediaDataset(
					dataset.getLatestVersion().getMetadataBlocks(),
					dataset.getLatestVersion().getDatasetPersistentId()));
		}

		return new MediaDatasetList().total(searchElements.getTotal()).items(mediaDatasets);
	}

}
