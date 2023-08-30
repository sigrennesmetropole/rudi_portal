package org.rudi.microservice.kalim.service.admin.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.rudi.facet.dataverse.bean.DatasetVersion;
import org.rudi.facet.dataverse.fields.FieldSpec;

class FieldUtils {

	private static final String VALUE = "value";

	private FieldUtils() {
	}

	static void setFieldValue(DatasetVersion datasetVersion, FieldSpec parentFieldSpec, FieldSpec fieldSpec,
			Object fieldValue) {
		setFieldValue(datasetVersion, parentFieldSpec, fieldSpec, () -> fieldValue, true);
	}

	static void setFieldValueIfNull(DatasetVersion datasetVersion, FieldSpec parentFieldSpec, FieldSpec fieldSpec,
			Supplier<Object> fieldValueSupplier) {
		setFieldValue(datasetVersion, parentFieldSpec, fieldSpec, fieldValueSupplier, false);
	}

	static void setFieldValue(DatasetVersion datasetVersion, FieldSpec parentFieldSpec, FieldSpec fieldSpec,
			Supplier<Object> fieldValueSupplier, boolean overwrite) {
		final var fieldOccurrences = getFieldOccurrences(datasetVersion, parentFieldSpec, fieldSpec);
		final var value = fieldValueSupplier.get();
		final var stringValue = value != null ? value.toString() : null;
		for (final var fieldOccurrence : fieldOccurrences) {
			if (overwrite || fieldOccurrence.get(VALUE) == null) {
				fieldOccurrence.put(VALUE, stringValue);
			}
		}
	}

	static Object getFieldValue(DatasetVersion datasetVersion, FieldSpec parentFieldSpec, FieldSpec fieldSpec) {
		final var fieldOccurrences = getFieldOccurrences(datasetVersion, parentFieldSpec, fieldSpec);
		return fieldOccurrences.get(0).get(VALUE);
	}

	@Nonnull
	static List<Map<String, Object>> getFieldOccurrences(DatasetVersion datasetVersion, FieldSpec parentFieldSpec,
			FieldSpec fieldSpec) {
		final var parentField = datasetVersion.getMetadataBlocks().getRudi().getFields().stream()
				.filter(field -> field.getTypeName().equals(parentFieldSpec.getName())).findFirst()
				.orElseThrow(() -> new FieldNotFoundException(parentFieldSpec));

		final List<Map<String, Object>> fieldOccurrences;
		final var parentFieldValue = parentField.getValue();
		if (parentFieldValue instanceof Map) {
			@SuppressWarnings("unchecked")
			final var parentFieldChildren = (Map<String, Map<String, Object>>) parentFieldValue;
			final var field = parentFieldChildren.get(fieldSpec.getName());
			fieldOccurrences = Collections.singletonList(field);
		} else if (parentFieldValue instanceof List) {
			@SuppressWarnings("unchecked")
			final var parentFieldValues = (List<Map<String, Map<String, Object>>>) parentFieldValue;
			return parentFieldValues.stream().map(parentFieldChildren -> {
				if (parentFieldChildren.containsKey(fieldSpec.getName())) {
					return parentFieldChildren.get(fieldSpec.getName());
				} else {
					throw new FieldNotFoundException(fieldSpec);
				}
			}).collect(Collectors.toList());
		} else {
			throw new FieldNotFoundException(fieldSpec);
		}
		return fieldOccurrences;
	}

}
