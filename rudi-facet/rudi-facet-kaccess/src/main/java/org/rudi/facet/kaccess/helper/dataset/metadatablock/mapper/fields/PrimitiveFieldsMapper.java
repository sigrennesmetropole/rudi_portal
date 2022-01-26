package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
abstract class PrimitiveFieldsMapper<T> {

	/**
	 * Types whose values will not be mapped using JSON serialisation
	 */
	private static final List<Class<?>> TYPES_NOT_MAPPED_TO_JSON_STRING = Arrays.asList(
			BigDecimal.class,
			Boolean.class,
			String.class
	);
	private final FieldGenerator fieldGenerator;
	protected final ObjectMapper objectMapper;

	public Map<String, Object> metadataToFields(T metadataElement) throws DataverseMappingException {
		final Map<String, Object> fields = new HashMap<>();
		metadataToFields(metadataElement, fields);
		return fields;
	}

	public abstract void metadataToFields(T metadataElement, Map<String, Object> fields) throws DataverseMappingException;

	public void createField(FieldSpec spec, Object value, Map<String, Object> fields) throws DataverseMappingException {
		if (value != null) {
			final DatasetMetadataBlockElementField field = fieldGenerator.generateField(spec, valueToString(value));
			if (field != null) {
				fields.put(field.getTypeName(), field);
			}
		}
	}

	private String valueToString(@Nonnull Object value) throws DataverseMappingException {
		if (isInstanceOfATypeNotMappedToJsonString(value)) {
			return value.toString();
		} else {
			return mapToJsonString(value);
		}
	}

	private boolean isInstanceOfATypeNotMappedToJsonString(@Nonnull Object value) {
		final Class<?> valueType = value.getClass();
		for (final Class<?> type : TYPES_NOT_MAPPED_TO_JSON_STRING) {
			if (type.isAssignableFrom(valueType)) {
				return true;
			}
		}
		return false;
	}

	private String mapToJsonString(Object value) throws DataverseMappingException {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new DataverseMappingException(e);
		}
	}

	/**
	 * @param primitiveRootField champ racine provenant de Dataverse (non null)
	 * @see #defaultMetadata() dans le cas où il n'y a aucun champ côté Dataverse
	 */
	@Nonnull
	public abstract T fieldToMetadata(@Nonnull Field primitiveRootField) throws DataverseMappingException;

	/**
	 * Injecte les métadonnées par défaut (avec toutes les propriétés requises par le Swagger) dans le cas où aucun champ (Field) n'existe pas côté Dataverse
	 *
	 * @return la valeur par défaut ou null si la métadonnée n'est pas obligatoire
	 * @see #fieldToMetadata(Field) dans le cas où le champ existe côté Dataverse
	 */
	@Nullable
	public abstract T defaultMetadata();
}
