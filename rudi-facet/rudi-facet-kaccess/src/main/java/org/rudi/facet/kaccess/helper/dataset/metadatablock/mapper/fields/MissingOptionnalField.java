package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.rudi.facet.dataverse.bean.FieldTypeClass;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

class MissingOptionnalField extends Field {
	public static final MissingOptionnalField INSTANCE = new MissingOptionnalField();

	@Override
	public String getTypeName() {
		return null;
	}

	@Override
	public FieldTypeClass getTypeClass() {
		return null;
	}

	@Override
	public Boolean getMultiple() {
		return false;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	List<CompoundValue> getCompoundValues() {
		return Collections.emptyList();
	}

	@Override
	boolean isMultiple() {
		return false;
	}

	@Nullable
	@Override
	String getValueAsString() {
		return null;
	}

	@Nullable
	@Override
	UUID getValueAsUUID() {
		return null;
	}

	@Nullable
	@Override
	Boolean getValueAsBoolean() {
		return null;
	}

	@Nullable
	@Override
	BigDecimal getValueAsBigDecimal() {
		return null;
	}

	@Override
	Long getValueAsLong() {
		return null;
	}

	@Nullable
	@Override
	<T> T getValueAs(Class<T> targetType, ObjectMapper objectMapper) {
		return null;
	}

	@Nullable
	@Override
	MapOfFields getValueAsMapOfFields() {
		return null;
	}

	@Nonnull
	@Override
	MultipleFieldValue getValueAsMultipleFieldValue() {
		return MultipleFieldValue.empty();
	}

	@Nonnull
	@Override
	<E extends Enum<E>> E getValueAsEnumWith(Function<String, E> valueOf) {
		throw new NotImplementedException();
	}

	@Nullable
	@Override
	<E> E getValueWith(Function<String, E> valueOf) {
		return null;
	}

	@Nullable
	@Override
	OffsetDateTime getValueAsOffsetDateTime(DateTimeMapper dateTimeMapper) {
		return null;
	}
}
