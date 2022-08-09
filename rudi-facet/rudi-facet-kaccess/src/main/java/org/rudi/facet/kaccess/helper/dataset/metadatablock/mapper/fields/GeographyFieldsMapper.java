package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataGeography;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.GEOGRAPHY;

@Component
class GeographyFieldsMapper extends SingleValuedFieldsMapper<MetadataGeography> {

	public GeographyFieldsMapper(FieldGenerator fieldGenerator, GeographyPrimitiveFieldsMapper primitiveFieldsMapper) {
		super(fieldGenerator, GEOGRAPHY, primitiveFieldsMapper);
	}

	@Override
	MetadataGeography getMetadataElement(Metadata metadata) {
		return metadata.getGeography();
	}

	@Override
	void setMetadataElement(@Nonnull Metadata metadata, @Nonnull MetadataGeography childMetadata) {
		metadata.setGeography(childMetadata);
	}

}
