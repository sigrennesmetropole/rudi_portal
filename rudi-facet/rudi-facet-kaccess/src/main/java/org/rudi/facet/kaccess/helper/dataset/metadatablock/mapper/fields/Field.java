package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.bean.FieldTypeClass;
import org.rudi.facet.dataverse.fields.FieldSpec;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
abstract class Field extends DatasetMetadataBlockElementField {

	@Nullable
	Field get(FieldSpec valueFieldSpec) {
		if (getTypeClass() == FieldTypeClass.COMPOUND && !isMultiple()) {
			final HashMap<?, ?> valueFields = (HashMap<?, ?>) getValue();
			final Object valueField = valueFields.get(valueFieldSpec.getName());
			return createFieldFrom(valueField);
		}
		throw new NotImplementedException(String.format("Field extraction not implemented for field : %s.%s", getTypeName(), valueFieldSpec.getName()));
	}

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
	Boolean getValueAsBoolean() {
		final String jsonValue = getValueAsString();
		return jsonValue == null ? null : Boolean.valueOf(jsonValue);
	}

	@Nullable
	BigDecimal getValueAsBigDecimal() {
		final String jsonValue = getValueAsString();
		return jsonValue == null ? null : new BigDecimal(jsonValue);
	}

	@Nullable
	<T> T getValueAs(Class<T> targetType, ObjectMapper objectMapper) throws DataverseMappingException {
		final JavaType javaType = objectMapper.getTypeFactory().constructType(targetType);
		return getValueAs(javaType, objectMapper);
	}

	@Nullable
	private <T> T getValueAs(JavaType javaType, ObjectMapper objectMapper) throws DataverseMappingException {
		final String jsonValue = getValueAsString();
		if (jsonValue == null) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonValue, javaType);
		} catch (JsonProcessingException e) {
			throw new DataverseMappingException(e);
		}
	}

	@NotNull <E> E getValueWith(Function<String, E> valueOf) {
		final String valueAsString = getValueAsString();
		if (valueAsString == null) {
			throw new IllegalArgumentException("Unexpected null value for Enum in field : " + getTypeName());
		}
		return valueOf.apply(valueAsString);
	}
}
