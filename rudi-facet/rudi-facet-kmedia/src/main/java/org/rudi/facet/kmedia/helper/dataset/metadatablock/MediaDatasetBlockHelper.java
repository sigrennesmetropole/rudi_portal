package org.rudi.facet.kmedia.helper.dataset.metadatablock;

import lombok.RequiredArgsConstructor;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.kmedia.bean.MediaDataset;
import org.rudi.facet.kmedia.helper.dataset.metadatablock.mapper.MediaDatasetCitationBlockMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MediaDatasetBlockHelper {

	private final MediaDatasetCitationBlockMapper citationMediaBlockBuilder;

	public DatasetMetadataBlock mediaDatasetToDatasetMetadataBlock(MediaDataset mediaDataset) throws DataverseMappingException {
		return new DatasetMetadataBlock()
				// tous les champs du media sont stockés dans lebloc citation
				.citation(citationMediaBlockBuilder.dataToDatasetMetadataBlockElement(mediaDataset));
	}

	public MediaDataset datasetMediaBlockToMediaDataset(DatasetMetadataBlock datasetMetadataBlock,
			String persistentId) {
		MediaDataset mediaDataset = new MediaDataset();
		mediaDataset.setDataverseDoi(persistentId);

		citationMediaBlockBuilder.datasetMetadataBlockElementToData(datasetMetadataBlock.getCitation(), mediaDataset);
		return mediaDataset;
	}
}
