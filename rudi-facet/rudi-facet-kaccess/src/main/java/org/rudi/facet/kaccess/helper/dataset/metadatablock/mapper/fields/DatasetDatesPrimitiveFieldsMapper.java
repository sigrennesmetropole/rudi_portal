package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.kaccess.bean.ReferenceDates;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_CREATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_DELETED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_EXPIRES;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_PUBLISHED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_UPDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_VALIDATED;

@Component
class DatasetDatesPrimitiveFieldsMapper extends PrimitiveFieldsMapper<ReferenceDates> {

	DatasetDatesPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper, DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	@Override
	public void metadataToFields(ReferenceDates dates, Map<String, Object> fields) throws DataverseMappingException {
		createDatesFields(dates, fields,
				DATASET_DATES_CREATED,
				DATASET_DATES_VALIDATED,
				DATASET_DATES_PUBLISHED,
				DATASET_DATES_UPDATED,
				DATASET_DATES_EXPIRES,
				DATASET_DATES_DELETED);
	}

	@Nonnull
	@Override
	public ReferenceDates fieldsToMetadata(@Nonnull MapOfFields fields) throws DataverseMappingException {
		return buildDates(fields,
				DATASET_DATES_CREATED,
				DATASET_DATES_VALIDATED,
				DATASET_DATES_PUBLISHED,
				DATASET_DATES_UPDATED,
				DATASET_DATES_EXPIRES,
				DATASET_DATES_DELETED);
	}

	@Nullable
	@Override
	public ReferenceDates defaultMetadata() {
		return null;
	}
}
