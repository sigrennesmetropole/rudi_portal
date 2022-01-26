package org.rudi.facet.kaccess.helper.dataset.metadatablock.generator;

import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.generators.AbstractDatasetMetadataBlockElementGenerator;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;

import java.util.Map;

class DatasetMetadataBlockElementGenerator extends AbstractDatasetMetadataBlockElementGenerator {

	DatasetMetadataBlockElementGenerator(Map<String, Object> metadatafields, DatasetMetadataBlockElementSpec blockElementSpec, FieldGenerator fieldGenerator) {
		super(metadatafields, blockElementSpec, fieldGenerator);
	}

}
