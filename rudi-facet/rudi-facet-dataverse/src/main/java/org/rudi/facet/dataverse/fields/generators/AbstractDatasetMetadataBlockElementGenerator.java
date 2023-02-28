package org.rudi.facet.dataverse.fields.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.exceptions.MismatchedChildrenValuesCount;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractDatasetMetadataBlockElementGenerator {
	private final Map<String, Object> metadatafields;
	private final DatasetMetadataBlockElementSpec blockElementSpec;
	private final FieldGenerator fieldGenerator;

	@Nonnull
	public DatasetMetadataBlockElement generateBlockElement() {
		final DatasetMetadataBlockElement block = new DatasetMetadataBlockElement()
				.fields(new ArrayList<>());
		blockElementSpec.streamLevel1Fields()
				.map(this::generateField)
				.filter(Objects::nonNull)
				.forEach(block::addFieldsItem);
		return block;
	}

	/**
	 * @param fieldSpec field specification
	 * @return the generated field or null if it should not be generated (no children)
	 */
	@Nullable
	private DatasetMetadataBlockElementField generateField(FieldSpec fieldSpec) {
		final Object fieldValue;
		// compound
		if (blockElementSpec.hasChildren(fieldSpec)) {
			if (fieldSpec.isMultiple()) {
				fieldValue = generateMultipleChildrenFields(fieldSpec);
			} else {
				fieldValue = generateChildrenFields(fieldSpec);
			}
		}
		// not compound
		else {
			if (fieldSpec.isMultiple()) {
				fieldValue = getStringValues(fieldSpec);
			} else {
				fieldValue = getStringValue(fieldSpec);
			}
		}

		return generateField(fieldSpec, fieldValue);
	}

	private int countChildrenValues(FieldSpec field) {
		return blockElementSpec.streamChildrenOf(field)
				.map(childField -> {
					final Object value = metadatafields.get(childField.getName());
					return applyIfList(value, List::size);
				})
				.filter(Objects::nonNull)
				.max((count1, count2) -> {
					if (!count1.equals(count2)) {
						throw new MismatchedChildrenValuesCount(field, count1, count2);
					}
					return count1;
				})
				.orElse(0);

	}

	@Nonnull
	private List<Map<String, DatasetMetadataBlockElementField>> generateMultipleChildrenFields(FieldSpec fieldSpec) {
		final int childrenCount = countChildrenValues(fieldSpec);
		final List<Map<String, DatasetMetadataBlockElementField>> childrenFields = new ArrayList<>(childrenCount);
		for (int i = 0; i < childrenCount; i++) {
			childrenFields.add(generateChildrenFields(fieldSpec, i));
		}
		return childrenFields;
	}

	@Nonnull
	private Map<String, DatasetMetadataBlockElementField> generateChildrenFields(FieldSpec parentFieldSpec) {
		return generateChildrenFields(parentFieldSpec, null);
	}

	@Nonnull
	private Map<String, DatasetMetadataBlockElementField> generateChildrenFields(FieldSpec parentFieldSpec, @Nullable Integer index) {
		return blockElementSpec.streamChildrenOf(parentFieldSpec)
				.map(fieldSpec -> {
					final String stringValue = getStringValue(fieldSpec, index);
					if (StringUtils.isNotEmpty(stringValue)) {
						return generateField(fieldSpec, stringValue);
					} else {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(DatasetMetadataBlockElementField::getTypeName, field -> field));
	}

	@Nonnull
	private List<String> getStringValues(FieldSpec fieldSpec) {
		final Object value = metadatafields.get(fieldSpec.getName());
		if (value == null) {
			return Collections.emptyList();
		}
		if (value instanceof List) {
			final List<Object> values = (List<Object>) value;
			return values.stream().map(Object::toString).collect(Collectors.toList());
		} else {
			return Collections.singletonList(toString(value));
		}
	}

	@Nullable
	private String getStringValue(FieldSpec fieldSpec) {
		final Object value = metadatafields.get(fieldSpec.getName());
		return toString(value, null);
	}

	@Nullable
	private String getStringValue(FieldSpec fieldSpec, @Nullable Integer index) {
		final Object value = metadatafields.get(fieldSpec.getName());
		return toString(value, index);
	}

	@Nullable
	private String toString(Object value) {
		return toString(value, null);
	}

	@Nullable
	private String toString(Object value, @Nullable Integer index) {
		if (index != null) {
			return applyIfList(value, values -> {
				final Object indexedValue = values.get(index);
				if (indexedValue != null) {
					return indexedValue.toString();
				} else {
					return null;
				}
			});
		} else if (value != null) {
			return value.toString();
		}
		return null;
	}

	@Nullable
	private <R> R applyIfList(Object value, Function<List<Object>, R> function) {
		if (value instanceof List) {
			//noinspection unchecked
			final List<Object> values = (List<Object>) value;
			return function.apply(values);
		} else {
			return null;
		}
	}

	@Nullable
	protected DatasetMetadataBlockElementField generateField(FieldSpec spec, Object value) {
		return fieldGenerator.generateField(spec, value);
	}

}
