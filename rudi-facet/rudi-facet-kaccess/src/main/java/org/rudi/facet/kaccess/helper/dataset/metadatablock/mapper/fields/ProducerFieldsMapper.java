package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.Organization;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER;

@Component
class ProducerFieldsMapper extends SingleValuedFieldsMapper<Organization> {

	ProducerFieldsMapper(FieldGenerator fieldGenerator, ProducerPrimitiveFieldsMapper primitiveFieldsMapper) {
		super(fieldGenerator, PRODUCER, primitiveFieldsMapper);
	}

	@Override
	@Nullable Organization getMetadataElement(Metadata metadata) {
		return metadata.getProducer();
	}

	@Override
	void setMetadataElement(@Nonnull Metadata metadata, @Nonnull Organization childMetadata) {
		metadata.setProducer(childMetadata);
	}
}
