package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.dataverse.utils.MessageUtils;
import org.rudi.facet.kaccess.bean.Contact;
import org.rudi.facet.kaccess.bean.Organization;
import org.rudi.facet.kaccess.bean.OrganizationOrganizationCoordinates;
import org.rudi.facet.kaccess.bean.ReferenceDates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
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
	final DateTimeMapper dateTimeMapper;

	public Map<String, Object> metadataToFields(T metadataElement) throws DataverseMappingException {
		final Map<String, Object> fields = new HashMap<>();
		metadataToFields(metadataElement, fields);
		return fields;
	}

	public abstract void metadataToFields(T metadataElement, Map<String, Object> fields) throws DataverseMappingException;

	public void createField(FieldSpec spec, Object value, Map<String, Object> fields) throws DataverseMappingException {
		if (value != null) {
			final Object fieldValue;
			if (spec.isMultiple()) {
				final List<Object> valueAsList = (List<Object>) value;
				final List<String> stringValues = new ArrayList<>();
				for (final var itemValue : valueAsList) {
					stringValues.add(valueToString(itemValue));
				}
				fieldValue = stringValues;
			} else {
				fieldValue = valueToString(value);
			}
			final DatasetMetadataBlockElementField field = fieldGenerator.generateField(spec, fieldValue);
			if (field != null) {
				fields.put(field.getTypeName(), field);
			}
		} else if (spec.isRequired()) {
			throw new NullPointerException(MessageUtils.buildErrorMessageRequiredMandatoryAttributes(spec));
		}
	}

	public void createField(FieldSpec spec, OffsetDateTime offsetDateTime, Map<String, Object> fields) throws DataverseMappingException {
		if (offsetDateTime != null) {
			final var value = dateTimeMapper.toDataverseTimestamp(offsetDateTime);
			createField(spec, value, fields);
		} else if (spec.isRequired()) {
			throw new NullPointerException(MessageUtils.buildErrorMessageRequiredMandatoryAttributes(spec));
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
	 * @param fields champs provenant de Dataverse (non null)
	 * @see #defaultMetadata() dans le cas où il n'y a aucun champ côté Dataverse
	 */
	@Nonnull
	public abstract T fieldsToMetadata(@Nonnull MapOfFields fields) throws DataverseMappingException;

	/**
	 * Injecte les métadonnées par défaut (avec toutes les propriétés requises par le Swagger) dans le cas où aucun champ (Field) n'existe pas côté Dataverse
	 *
	 * @return la valeur par défaut ou null si la métadonnée n'est pas obligatoire
	 * @see #fieldsToMetadata(MapOfFields) dans le cas où le champ existe côté Dataverse
	 */
	@Nullable
	public abstract T defaultMetadata();

	protected void createDatesFields(@Nullable ReferenceDates dates, Map<String, Object> fields,
			FieldSpec createdField,
			FieldSpec validatedField,
			FieldSpec publishedField,
			FieldSpec updatedField,
			FieldSpec expiresField,
			FieldSpec deletedField
	) throws DataverseMappingException {
		if (dates != null) {
			createField(createdField, dates.getCreated(), fields);
			createField(validatedField, dates.getValidated(), fields);
			createField(publishedField, dates.getPublished(), fields);
			createField(updatedField, dates.getUpdated(), fields);
			createField(expiresField, dates.getExpires(), fields);
			createField(deletedField, dates.getDeleted(), fields);
		}
	}

	protected ReferenceDates buildDates(MapOfFields fields,
			FieldSpec createdField,
			FieldSpec validatedField,
			FieldSpec publishedField,
			FieldSpec updatedField,
			FieldSpec expiresField,
			FieldSpec deletedField) {
		return new ReferenceDates()
				.created(fields.get(createdField).getValueAsOffsetDateTime(dateTimeMapper))
				.validated(fields.get(validatedField).getValueAsOffsetDateTime(dateTimeMapper))
				.published(fields.get(publishedField).getValueAsOffsetDateTime(dateTimeMapper))
				.updated(fields.get(updatedField).getValueAsOffsetDateTime(dateTimeMapper))
				.expires(fields.get(expiresField).getValueAsOffsetDateTime(dateTimeMapper))
				.deleted(fields.get(deletedField).getValueAsOffsetDateTime(dateTimeMapper))
				;
	}

	protected void createContactFields(@Nullable Contact contact, Map<String, Object> fields,
			FieldSpec contactIdField,
			FieldSpec contactNameField,
			FieldSpec emailField,
			FieldSpec organizationNameField,
			FieldSpec roleField,
			FieldSpec contactSummaryField) throws DataverseMappingException {
		if (contact != null) {
			createField(contactIdField, contact.getContactId().toString(), fields);
			createField(contactNameField, contact.getContactName(), fields);
			createField(emailField, contact.getEmail(), fields);
			createField(organizationNameField, contact.getOrganizationName(), fields);
			createField(roleField, contact.getRole(), fields);
			createField(contactSummaryField, contact.getContactSummary(), fields);
		}
	}

	protected Contact buildContact(@Nonnull MapOfFields fields,
			FieldSpec contactIdField,
			FieldSpec contactNameField,
			FieldSpec emailField,
			FieldSpec organizationNameField,
			FieldSpec roleField,
			FieldSpec contactSummaryField) {
		return new Contact()
				.contactId(fields.get(contactIdField).getValueAsUUID())
				.contactName(fields.get(contactNameField).getValueAsString())
				.email(fields.get(emailField).getValueAsString())
				.organizationName(fields.get(organizationNameField).getValueAsString())
				.role(fields.get(roleField).getValueAsString())
				.contactSummary(fields.get(contactSummaryField).getValueAsString());
	}

	protected void createOrganizationFields(@Nullable Organization organization, Map<String, Object> fields,
			FieldSpec organizationNameField,
			FieldSpec organizationAddressField,
			FieldSpec organizationIdField,
			FieldSpec latitudeField, FieldSpec longitudeField,
			FieldSpec organizationCaptionField,
			FieldSpec organizationSummaryField) throws DataverseMappingException {
		if (organization != null) {
			createField(organizationIdField, organization.getOrganizationId().toString(), fields);
			createField(organizationNameField, organization.getOrganizationName(), fields);
			createField(organizationAddressField, organization.getOrganizationAddress(), fields);
			createCoordinatesFields(organization.getOrganizationCoordinates(), fields, latitudeField, longitudeField);
			createField(organizationCaptionField, organization.getOrganizationCaption(), fields);
			createField(organizationSummaryField, organization.getOrganizationSummary(), fields);
		}
	}

	private void createCoordinatesFields(@Valid OrganizationOrganizationCoordinates organizationCoordinates, Map<String, Object> fields, FieldSpec latitudeField, FieldSpec longitudeField) throws DataverseMappingException {
		if (organizationCoordinates != null) {
			createField(latitudeField, organizationCoordinates.getLatitude(), fields);
			createField(longitudeField, organizationCoordinates.getLongitude(), fields);
		}
	}

	protected Organization buildOrganization(MapOfFields fields,
			FieldSpec organizationNameField,
			FieldSpec organizationAddressField,
			FieldSpec organizationIdField,
			FieldSpec latitudeField, FieldSpec longitudeField,
			FieldSpec organizationCaptionField,
			FieldSpec organizationSummaryField) {
		final var organization = new Organization()
				.organizationName(fields.get(organizationNameField).getValueAsString())
				.organizationAddress(fields.get(organizationAddressField).getValueAsString())
				.organizationId(fields.get(organizationIdField).getValueAsUUID())
				.organizationCoordinates(ObjectsUtils.nullIfEmpty(new OrganizationOrganizationCoordinates()
						.latitude(fields.get(latitudeField).getValueAsBigDecimal())
						.longitude(fields.get(longitudeField).getValueAsBigDecimal())))
				.organizationCaption(fields.get(organizationCaptionField).getValueAsString())
				.organizationSummary(fields.get(organizationSummaryField).getValueAsString());
		return ObjectsUtils.nullIfEmpty(organization);
	}

}
