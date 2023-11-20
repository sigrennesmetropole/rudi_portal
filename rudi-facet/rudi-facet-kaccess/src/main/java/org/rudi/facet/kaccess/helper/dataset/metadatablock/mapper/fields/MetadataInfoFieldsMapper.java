package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataMetadataInfo;
import org.springframework.stereotype.Component;

@Component
class MetadataInfoFieldsMapper extends SingleValuedFieldsMapper<MetadataMetadataInfo> {
	static final int RANK = DEFAULT_RANK;

	MetadataInfoFieldsMapper(FieldGenerator fieldGenerator, MetadataInfoPrimitiveFieldsMapper primitiveFieldsMapper) {
		super(fieldGenerator, METADATA_INFO, primitiveFieldsMapper);
	}

	@Override
	@Nullable
	MetadataMetadataInfo getMetadataElement(Metadata metadata) {
		return metadata.getMetadataInfo();
	}

	@Override
	void setMetadataElement(@Nonnull Metadata metadata, @Nonnull MetadataMetadataInfo childMetadata) {
		metadata.setMetadataInfo(childMetadata);
	}

	@Override
	public int getRank() {
		return RANK;
	}
}
