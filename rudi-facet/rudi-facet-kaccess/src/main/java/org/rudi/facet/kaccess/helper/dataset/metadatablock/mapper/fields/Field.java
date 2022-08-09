package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.bean.FieldTypeClass;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Champ Dataverse. Exemple du champ <code>rudi_access_condition_usage_constraint_language</code> :
 *
 * <pre>{@code
 * {
 *   "typeName": "rudi_access_condition_usage_constraint_language",
 *   "typeClass": "primitive",
 *   "multiple": false,
 *   "value": "fr-FR"
 * }
 * }</pre>
 */
abstract class Field {

	public abstract String getTypeName();
	public abstract FieldTypeClass getTypeClass();
	public abstract Boolean getMultiple();
	public abstract Object getValue();

	@Nullable
	static Field createFieldFrom(Object valueFieldObject) {
		if (valueFieldObject == null) {
			return null;
		} else if (valueFieldObject instanceof DatasetMetadataBlockElementField) {
			final DatasetMetadataBlockElementField valueField = (DatasetMetadataBlockElementField) valueFieldObject;
			return new RootField(valueField);
		} else {
			final HashMap<?, ?> valueField = (HashMap<?, ?>) valueFieldObject;
			return new ChildField(valueField);
		}
	}

	List<CompoundValue> getCompoundValues() {
		if (getTypeClass() == FieldTypeClass.COMPOUND && isMultiple()) {
			final List<?> valueFields = (List<?>) getValue();
			if (valueFields == null) {
				return Collections.emptyList();
			}
			return valueFields.stream().map(valueField -> {
				final Map<?, ?> valueFieldMap = (Map<?, ?>) valueField;
				return new CompoundValue(valueFieldMap);
			}).collect(Collectors.toList());
		}
		throw new NotImplementedException(String.format("Fields extraction not implemented for field : %s", getTypeName()));
	}

	boolean isMultiple() {
		return Boolean.TRUE.equals(getMultiple());
	}

	@Nullable
	String getValueAsString() {
		final Object jsonValue = getValue();
		return jsonValue == null ? null : jsonValue.toString();
	}

	@Nullable
	UUID getValueAsUUID() {
		final Object jsonValue = getValue();
		return jsonValue == null ? null : UUID.fromString(jsonValue.toString());
	}

	@Nullable
	Boolean getValueAsBoolean() {
		final var jsonValue = getValueAsString();
		return jsonValue == null ? null : Boolean.valueOf(jsonValue);
	}

	@Nullable
	BigDecimal getValueAsBigDecimal() {
		final var jsonValue = getValueAsString();
		return jsonValue == null ? null : new BigDecimal(jsonValue);
	}

	Long getValueAsLong() {
		return getValueWith(Long::valueOf);
	}

	@Nullable
	<T> T getValueAs(Class<T> targetType, ObjectMapper objectMapper) throws DataverseMappingException {
		final var javaType = objectMapper.getTypeFactory().constructType(targetType);
		return getValueAs(javaType, objectMapper);
	}

	@Nullable
	<T> List<T> getValueAsListOf(Class<T> targetType, ObjectMapper objectMapper) throws DataverseMappingException {
		final var javaType = objectMapper.getTypeFactory().constructParametricType(List.class, targetType);
		return getValueAs(javaType, objectMapper);
	}

	@Nullable
	MapOfFields getValueAsMapOfFields() {
		final Object value = getValue();
		if (value == null) {
			return null;
		}
		final var valueAsMap = (Map<String, Object>) value;
		return MapOfFields.from(valueAsMap);
	}

	@Nonnull
	MultipleFieldValue getValueAsMultipleFieldValue() {
		final Object value = getValue();
		if (value == null) {
			return MultipleFieldValue.empty();
		}
		final var valueAsListOfMaps = (List<Map<String, Object>>) value;
		return MultipleFieldValue.from(valueAsListOfMaps);
	}

	@Nullable
	private <T> T getValueAs(JavaType javaType, ObjectMapper objectMapper) throws DataverseMappingException {
		final var jsonValue = getValueAsString();
		if (jsonValue == null) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonValue, javaType);
		} catch (JsonProcessingException e) {
			throw new DataverseMappingException(e);
		}
	}

	@Nonnull <E extends Enum<E>> E getValueAsEnumWith(Function<String, E> valueOf) {
		final var valueAsString = getValueAsString();
		if (valueAsString == null) {
			throw new IllegalArgumentException("Unexpected null value for Enum in field : " + getTypeName());
		}
		return valueOf.apply(valueAsString);
	}

	@Nullable <E> E getValueWith(Function<String, E> valueOf) {
		final var valueAsString = getValueAsString();
		if (valueAsString == null) {
			return null;
		}
		return valueOf.apply(valueAsString);
	}

	@Nullable
	OffsetDateTime getValueAsOffsetDateTime(DateTimeMapper dateTimeMapper) {
		return getValueWith(dateTimeMapper::fromDataverseTimestamp);
	}
}
