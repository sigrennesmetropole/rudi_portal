package org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.utils.MessageUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class AbstractMetadataBlockElementMapper<T> implements MetadataBlockElementMapper<T> {
	private final FieldGenerator fieldGenerator;
	private final DateTimeMapper dateTimeMapper;

	protected DatasetMetadataBlockElementField createField(FieldSpec fieldSpec, Object value) {
		return fieldGenerator.generateField(fieldSpec, value);
	}

	protected void addOptionalPrimitiveField(String value, Map<String, Object> map, FieldSpec fieldSpec) {
		if (!StringUtils.isEmpty(value)) {
			DatasetMetadataBlockElementField elementField = fieldGenerator.generateField(fieldSpec, value);
			map.put(fieldSpec.getName(), elementField);
		}
	}

	@Override
	public void addOptionalPrimitiveField(Object value, Map<String, Object> map, FieldSpec fieldSpec) {
		String stringValue = Objects.toString(value, StringUtils.EMPTY);
		addOptionalPrimitiveField(stringValue, map, fieldSpec);
	}

	protected void addOptionalControlledField(String value, Map<String, Object> map, FieldSpec fieldSpec) {
		if (!StringUtils.isEmpty(value)) {
			DatasetMetadataBlockElementField elementField = fieldGenerator.generateField(fieldSpec, value);
			map.put(fieldSpec.getName(), elementField);
		}
	}

	protected void addMandatoryPrimitiveField(Object value, Map<String, Object> map, FieldSpec fieldSpec) {
		Objects.requireNonNull(value, MessageUtils.buildErrorMessageRequiredMandatoryAttributes(fieldSpec));
		String stringValue = Objects.toString(value, StringUtils.EMPTY);
		addOptionalPrimitiveField(stringValue, map, fieldSpec);
	}

	protected void addOptionalDateTimeField(OffsetDateTime dateTime, Map<String, Object> map, FieldSpec fieldSpec) {
		if (dateTime != null) {
			DatasetMetadataBlockElementField dateTimeField = fieldGenerator.generateField(fieldSpec, dateTimeMapper.toDataverseTimestamp(dateTime));
			map.put(fieldSpec.getName(), dateTimeField);
		}
	}

	protected void addMandatoryDateTimeField(OffsetDateTime dateTime, Map<String, Object> map, FieldSpec fieldSpec) {
		Objects.requireNonNull(dateTime, MessageUtils.buildErrorMessageRequiredMandatoryAttributes(fieldSpec));
		addMandatoryPrimitiveField(dateTimeMapper.toDataverseTimestamp(dateTime), map, fieldSpec);
	}

	protected DatasetMetadataBlockElementField getField(List<DatasetMetadataBlockElementField> fields, FieldSpec spec) {
		if (!CollectionUtils.isEmpty(fields)) {
			Optional<DatasetMetadataBlockElementField> fieldOptional = fields.stream().filter(
					datasetMetadataBlockElementField -> datasetMetadataBlockElementField.getTypeName().equals(spec.getName()))
					.findFirst();
			if (fieldOptional.isPresent()) {
				return fieldOptional.get();
			}
		}
		return null;
	}

	protected String getPrimitiveFieldValue(DatasetMetadataBlockElementField field) {
		return field != null && field.getValue() != null ? field.getValue().toString() : "";
	}

	protected OffsetDateTime getOffsetDateTimeFieldValue(DatasetMetadataBlockElementField dateTimeField) {
		OffsetDateTime offsetDateTime = null;

		if (dateTimeField != null && dateTimeField.getValue() != null) {

			offsetDateTime = dateTimeMapper.fromDataverseTimestamp(dateTimeField.getValue().toString());
		}
		return offsetDateTime;
	}

	protected Integer getIntegerFieldValue(DatasetMetadataBlockElementField integerField) {
		Integer integerValue = null;

		if (integerField != null && integerField.getValue() != null) {

			String stringValue = getPrimitiveFieldValue(integerField);
			integerValue = (stringValue == null) ? null : Integer.valueOf(stringValue);
		}
		return integerValue;
	}
}
