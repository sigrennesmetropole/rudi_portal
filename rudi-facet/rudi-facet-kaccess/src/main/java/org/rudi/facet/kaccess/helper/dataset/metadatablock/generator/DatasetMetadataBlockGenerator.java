package org.rudi.facet.kaccess.helper.dataset.metadatablock.generator;

import lombok.RequiredArgsConstructor;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.constant.CitationMetadataField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.fields.generators.IDatasetMetadataBlockGenerator;
import org.rudi.facet.kaccess.constant.RudiMetadataField;

import java.util.Map;

import static org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.CitationMetadataBlockMapper.CITATION_DISPLAY_NAME;
import static org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.RudiMetadataBlockMapper.RUDI_DISPLAY_NAME;

@RequiredArgsConstructor
public class DatasetMetadataBlockGenerator implements IDatasetMetadataBlockGenerator {

	private final DatasetMetadataBlockElementGenerator citationGenerator;
	private final DatasetMetadataBlockElementGenerator rudiGenerator;

	public DatasetMetadataBlockGenerator(Map<String, Object> metadatafields, FieldGenerator fieldGenerator) {
		this.citationGenerator = new DatasetMetadataBlockElementGenerator(metadatafields, CitationMetadataField.CITATION_ELEMENT_SPEC, fieldGenerator);
		this.rudiGenerator = new DatasetMetadataBlockElementGenerator(metadatafields, RudiMetadataField.RUDI_ELEMENT_SPEC, fieldGenerator);
	}

	@Override
	public DatasetMetadataBlock generateBlock() {
		return new DatasetMetadataBlock()
				.citation(citationGenerator.generateBlockElement()
						.displayName(CITATION_DISPLAY_NAME))
				.rudi(rudiGenerator.generateBlockElement()
						.displayName(RUDI_DISPLAY_NAME));
	}

}
