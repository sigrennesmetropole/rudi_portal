package org.rudi.facet.kaccess.helper.dataset.metadatablock;

import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.CitationMetadataBlockMapper;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.RudiMetadataBlockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetadataBlockHelper {

	@Autowired
	private CitationMetadataBlockMapper citationMetadataBlockBuilder;

	@Autowired
	private RudiMetadataBlockMapper rudiMetadataBlockBuilder;

	public DatasetMetadataBlock metadataToDatasetMetadataBlock(Metadata metadata) throws DataverseMappingException {
		return new DatasetMetadataBlock()
				.citation(citationMetadataBlockBuilder.dataToDatasetMetadataBlockElement(metadata))
				.rudi(rudiMetadataBlockBuilder.dataToDatasetMetadataBlockElement(metadata));
	}

	public Metadata datasetMetadataBlockToMetadata(DatasetMetadataBlock datasetMetadataBlock, String persistentId) throws DataverseMappingException {
		Metadata metadata = new Metadata();
		metadata.setDataverseDoi(persistentId);
		citationMetadataBlockBuilder.datasetMetadataBlockElementToData(datasetMetadataBlock.getCitation(), metadata);
		rudiMetadataBlockBuilder.datasetMetadataBlockElementToData(datasetMetadataBlock.getRudi(), metadata);
		return metadata;
	}
}
