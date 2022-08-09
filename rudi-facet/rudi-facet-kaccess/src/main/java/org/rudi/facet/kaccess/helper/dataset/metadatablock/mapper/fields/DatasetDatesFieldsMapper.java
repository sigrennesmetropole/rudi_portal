package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.ReferenceDates;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES;

@Component
class DatasetDatesFieldsMapper extends SingleValuedFieldsMapper<ReferenceDates> {

	DatasetDatesFieldsMapper(FieldGenerator fieldGenerator, DatasetDatesPrimitiveFieldsMapper primitiveFieldsMapper) {
		super(fieldGenerator, DATASET_DATES, primitiveFieldsMapper);
	}

	@Override
	@Nullable ReferenceDates getMetadataElement(Metadata metadata) {
		return metadata.getDatasetDates();
	}

	@Override
	void setMetadataElement(@Nonnull Metadata metadata, @Nonnull ReferenceDates childMetadata) {
		metadata.setDatasetDates(childMetadata);
	}
}
