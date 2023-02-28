package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataExtMetadata;
import org.rudi.facet.kaccess.bean.MetadataExtMetadataExtSelfdata;
import org.springframework.stereotype.Component;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.EXT_SELFDATA_CONTENT;

@Component
public class ExtMetadataExtSelfdataFieldsMapper extends SingleValuedFieldsMapper<MetadataExtMetadataExtSelfdata> {

	ExtMetadataExtSelfdataFieldsMapper(FieldGenerator fieldGenerator, PrimitiveFieldsMapper<MetadataExtMetadataExtSelfdata> primitiveFieldsMapper) {
		super(fieldGenerator, EXT_SELFDATA_CONTENT, primitiveFieldsMapper);
	}

	@Override
	@Nullable
	MetadataExtMetadataExtSelfdata getMetadataElement(Metadata metadata) {
		return metadata.getExtMetadata() != null ? metadata.getExtMetadata().getExtSelfdata() : null;
	}

	@Override
	void setMetadataElement(@NotNull Metadata metadata, @NotNull MetadataExtMetadataExtSelfdata childMetadata) {
		metadata.setExtMetadata(new MetadataExtMetadata().extSelfdata(childMetadata));
	}
}
