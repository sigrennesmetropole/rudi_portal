package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Metadata;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA;

@Component
class MediaFieldsMapper extends MultipleValuedFieldsMapper<Media> {
	MediaFieldsMapper(FieldGenerator fieldGenerator, PrimitiveFieldsMapper<Media> primitiveFieldsMapper) {
		super(fieldGenerator, MEDIA, primitiveFieldsMapper);
	}

	@Override
	@Nullable List<Media> getMetadataElements(Metadata metadata) {
		return metadata.getAvailableFormats();
	}

	@Override
	void setMetadataElements(@Nonnull Metadata metadata, @Nonnull List<Media> childrenMetadata) {
		metadata.setAvailableFormats(childrenMetadata);
	}
}
