package org.rudi.facet.kaccess.helper.search.mapper;

import lombok.RequiredArgsConstructor;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.generator.DatasetMetadataBlockGenerator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
class MetadatafieldsToBlocksMapper {
	private final FieldGenerator fieldGenerator;

	DatasetMetadataBlock map(Map<String, Object> metadatafields) {
		return new DatasetMetadataBlockGenerator(metadatafields, fieldGenerator).generateBlock();
	}

}
