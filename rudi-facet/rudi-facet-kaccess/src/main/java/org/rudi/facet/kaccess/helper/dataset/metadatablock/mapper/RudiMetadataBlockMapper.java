package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.AbstractMetadataBlockElementMapper;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.dataverse.utils.MessageUtils;
import org.rudi.facet.kaccess.bean.Connector;
import org.rudi.facet.kaccess.bean.Contact;
import org.rudi.facet.kaccess.bean.DatasetSize;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.HashAlgorithm;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.kaccess.bean.Media.MediaTypeEnum;
import org.rudi.facet.kaccess.bean.MediaFile;
import org.rudi.facet.kaccess.bean.MediaFileAllOfChecksum;
import org.rudi.facet.kaccess.bean.MediaSeries;
import org.rudi.facet.kaccess.bean.MediaType;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.Metadata.StorageStatusEnum;
import org.rudi.facet.kaccess.bean.MetadataMetadataInfo;
import org.rudi.facet.kaccess.bean.MetadataTemporalSpread;
import org.rudi.facet.kaccess.bean.Organization;
import org.rudi.facet.kaccess.bean.ReferenceDates;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields.FieldsMapper;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields.RootFields;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_EMAIL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_ROLE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_CREATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_DELETED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_PUBLISHED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_UPDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_DATES_VALIDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_SIZE_NUMBER_OF_FIELDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_SIZE_NUMBER_OF_RECORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DOI;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_CHECKSUM_ALGO;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_CHECKSUM_HASH;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_ENCODING;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_STRUCTURE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.FILE_TYPE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GLOBAL_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.KEYWORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.LOCAL_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_CONNECTOR_INTERFACE_CONTRACT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_CONNECTOR_URL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.MEDIA_TYPE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_API_VERSION;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_EMAIL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_ROLE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_CREATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_DELETED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_PUBLISHED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_UPDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_DATES_VALIDATED;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_PROVIDER_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_ADDRESS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.PRODUCER_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESOURCE_LANGUAGES;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESOURCE_TITLE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_CURENT_NUMBER_OF_RECORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_CURENT_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_LATENCY;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_PERIOD;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_TOTAL_NUMBER_OF_RECORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SERIES_TOTAL_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.STORAGE_STATUS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SUMMARY;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SUMMARY_LANGUAGE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SUMMARY_TEXT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SYNOPSIS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SYNOPSIS_LANGUAGE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.SYNOPSIS_TEXT;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TEMPORAL_SPREAD;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TEMPORAL_SPREAD_END_DATE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.TEMPORAL_SPREAD_START_DATE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.THEME;

@Component
public class RudiMetadataBlockMapper extends AbstractMetadataBlockElementMapper<Metadata> {

	/*
	 * Display name
	 */
	public static final String RUDI_DISPLAY_NAME = "Rudi Metadata";

	private final ObjectMapper objectMapper = new ObjectMapper();

	private final Collection<FieldsMapper<?>> fieldsMappers;

	public RudiMetadataBlockMapper(FieldGenerator fieldGenerator, DateTimeMapper dateTimeMapper, Collection<FieldsMapper<?>> fieldsMappers) {
		super(fieldGenerator, dateTimeMapper);
		this.fieldsMappers = fieldsMappers;
	}

	@Override
	public void datasetMetadataBlockElementToData(DatasetMetadataBlockElement datasetMetadataBlockElement,
			Metadata metadataParameter) throws DataverseMappingException {
		final Metadata metadata = ObjectUtils.firstNonNull(metadataParameter, new Metadata());

		List<DatasetMetadataBlockElementField> rudiFields = datasetMetadataBlockElement.getFields();

		setGlobalId(metadata, getField(rudiFields, GLOBAL_ID));
		setLocalId(metadata, getField(rudiFields, LOCAL_ID));
		setDoiField(metadata, getField(rudiFields, DOI));
		setResourceTitle(metadata, getField(rudiFields, RESOURCE_TITLE));
		setSynopsis(metadata, getField(rudiFields, SYNOPSIS));
		setSummary(metadata, getField(rudiFields, SUMMARY));
		setTheme(metadata, getField(rudiFields, THEME));
		setKeywords(metadata, getField(rudiFields, KEYWORDS));
		setProducer(metadata, getField(rudiFields, PRODUCER));
		setContacts(metadata, getField(rudiFields, CONTACT));
		setAvailableFormats(metadata, getField(rudiFields, MEDIA));
		setResourceLanguages(metadata, getField(rudiFields, RESOURCE_LANGUAGES));
		setTemporalSpread(metadata, getField(rudiFields, TEMPORAL_SPREAD));
		setDatasetSize(metadata, getField(rudiFields, DATASET_SIZE));
		setDatasetDates(metadata, getField(rudiFields, DATASET_DATES));
		setStorageStatus(metadata, getField(rudiFields, STORAGE_STATUS));
		setMetadataInfo(metadata, rudiFields, getField(rudiFields, METADATA_INFO));

		final RootFields allRudiRootFields = new RootFields(rudiFields);
		for (final FieldsMapper<?> fieldsMapper : fieldsMappers) {
			fieldsMapper.fieldsToMetadata(allRudiRootFields, metadata);
		}
	}

	@Override
	public String getDisplayName() {
		return RUDI_DISPLAY_NAME;
	}

	@Override
	public List<DatasetMetadataBlockElementField> createFields(final Metadata metadata) throws DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();

		addGlobalIdField(metadata, fields);
		addLocalIdField(metadata, fields);
		addDoiField(metadata, fields);
		addResourceTitleField(metadata, fields);
		addSynopsisField(metadata, fields);
		addSummaryField(metadata, fields);
		addThemeField(metadata, fields);
		addKeywordsField(metadata, fields);
		addProducerField(metadata, fields);
		addContactsField(metadata, fields);
		addAvailableFormatsField(metadata, fields);
		addResourceLanguagesField(metadata, fields);
		addTemporalSpreadField(metadata, fields);
		addDatasetSizeField(metadata, fields);
		addDatasetDatesField(metadata, fields);
		addStorageStatusField(metadata, fields);
		addMetadataInfoField(metadata, fields);

		for (final FieldsMapper<?> fieldsMapper : fieldsMappers) {
			fieldsMapper.metadataToFields(metadata, fields);
		}

		return fields;
	}

	private void addGlobalIdField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		Objects.requireNonNull(metadata.getGlobalId(), MessageUtils.buildErrorMessageRequiredMandatoryAttributes(GLOBAL_ID));
		String id = metadata.getGlobalId().toString();
		DatasetMetadataBlockElementField field = createField(GLOBAL_ID, id);
		fields.add(field);
	}

	private void setGlobalId(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {
			metadata.setGlobalId(UUID.fromString(field.getValue().toString()));
		}
	}

	private void addLocalIdField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		String localId = metadata.getLocalId();
		if (!StringUtils.isEmpty(localId)) {
			DatasetMetadataBlockElementField localIdField = createField(LOCAL_ID, localId);
			fields.add(localIdField);
		}
	}

	private void setLocalId(Metadata metadata, DatasetMetadataBlockElementField field) {
		metadata.setLocalId(getPrimitiveFieldValue(field));
	}

	private void addDoiField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		String doi = metadata.getDoi();
		if (!StringUtils.isEmpty(doi)) {
			DatasetMetadataBlockElementField doiField = createField(DOI, doi);
			fields.add(doiField);
		}
	}

	private void setDoiField(Metadata metadata, DatasetMetadataBlockElementField field) {
		metadata.setDoi(getPrimitiveFieldValue(field));
	}

	private void addResourceTitleField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		DatasetMetadataBlockElementField titleField = createField(RESOURCE_TITLE, metadata.getResourceTitle());
		fields.add(titleField);
	}

	private void setResourceTitle(Metadata metadata, DatasetMetadataBlockElementField field) {
		metadata.setResourceTitle(getPrimitiveFieldValue(field));
	}

	private void addThemeField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		DatasetMetadataBlockElementField themeField = createField(THEME, metadata.getTheme());
		fields.add(themeField);
	}

	private void setTheme(Metadata metadata, DatasetMetadataBlockElementField field) {
		metadata.setTheme(getPrimitiveFieldValue(field));
	}

	private void addKeywordsField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		List<String> keywords = metadata.getKeywords();
		if (!CollectionUtils.isEmpty(keywords)) {
			DatasetMetadataBlockElementField keywordField = createField(KEYWORDS, keywords);
			fields.add(keywordField);
		}
	}

	private void setKeywords(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {
			List<?> keywordValues = (List<?>) field.getValue();
			metadata.setKeywords(keywordValues.stream().map(Object::toString).collect(Collectors.toList()));
		}
	}

	private void addTemporalSpreadField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		MetadataTemporalSpread temporalSpread = metadata.getTemporalSpread();

		if (temporalSpread != null) {
			Objects.requireNonNull(temporalSpread.getStartDate(), MessageUtils.buildErrorMessageRequiredMandatoryAttributes(TEMPORAL_SPREAD_START_DATE));

			Map<String, Object> temporalSpreadValue = new HashMap<>();

			addMandatoryDateTimeField(temporalSpread.getStartDate(), temporalSpreadValue, TEMPORAL_SPREAD_START_DATE);
			addOptionalDateTimeField(temporalSpread.getEndDate(), temporalSpreadValue, TEMPORAL_SPREAD_END_DATE);

			DatasetMetadataBlockElementField temporalSpreadField = createField(TEMPORAL_SPREAD, temporalSpreadValue);

			fields.add(temporalSpreadField);
		}
	}

	private void setTemporalSpread(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			HashMap<?, ?> temporalSpreadValues = (HashMap<?, ?>) field.getValue();

			DatasetMetadataBlockElementField startDateField = objectMapper.convertValue(
					temporalSpreadValues.get(TEMPORAL_SPREAD_START_DATE.getName()), DatasetMetadataBlockElementField.class);
			final var startDate = getOffsetDateTimeFieldValue(startDateField);

			DatasetMetadataBlockElementField endDateField = objectMapper.convertValue(
					temporalSpreadValues.get(TEMPORAL_SPREAD_END_DATE.getName()), DatasetMetadataBlockElementField.class);
			final var endDate = getOffsetDateTimeFieldValue(endDateField);

			MetadataTemporalSpread temporalSpread = new MetadataTemporalSpread().startDate(startDate).endDate(endDate);

			metadata.setTemporalSpread(temporalSpread);
		}
	}

	private void addDatasetDatesField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		ReferenceDates datesDataset = metadata.getDatasetDates();
		Objects.requireNonNull(datesDataset, MessageUtils.buildErrorMessageRequiredMandatoryAttributes(DATASET_DATES));

		Map<String, Object> datasetDateValue = new HashMap<>();

		addMandatoryDateTimeField(datesDataset.getCreated(), datasetDateValue, DATASET_DATES_CREATED);
		addOptionalDateTimeField(datesDataset.getValidated(), datasetDateValue, DATASET_DATES_VALIDATED);
		addOptionalDateTimeField(datesDataset.getPublished(), datasetDateValue, DATASET_DATES_PUBLISHED);
		addMandatoryDateTimeField(datesDataset.getUpdated(), datasetDateValue, DATASET_DATES_UPDATED);
		addOptionalDateTimeField(datesDataset.getDeleted(), datasetDateValue, DATASET_DATES_DELETED);

		DatasetMetadataBlockElementField datasetDateField = createField(DATASET_DATES, datasetDateValue);

		fields.add(datasetDateField);
	}

	private void setDatasetDates(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			HashMap<?, ?> temporalSpreadValues = (HashMap<?, ?>) field.getValue();

			DatasetMetadataBlockElementField createdDateField = objectMapper.convertValue(
					temporalSpreadValues.get(DATASET_DATES_CREATED.getName()), DatasetMetadataBlockElementField.class);
			final var created = getOffsetDateTimeFieldValue(createdDateField);

			DatasetMetadataBlockElementField validatedDateField = objectMapper.convertValue(
					temporalSpreadValues.get(DATASET_DATES_VALIDATED.getName()), DatasetMetadataBlockElementField.class);
			final var validated = getOffsetDateTimeFieldValue(validatedDateField);

			DatasetMetadataBlockElementField publishedDateField = objectMapper.convertValue(
					temporalSpreadValues.get(DATASET_DATES_PUBLISHED.getName()), DatasetMetadataBlockElementField.class);
			final var published = getOffsetDateTimeFieldValue(publishedDateField);

			DatasetMetadataBlockElementField updatedDateField = objectMapper.convertValue(
					temporalSpreadValues.get(DATASET_DATES_UPDATED.getName()), DatasetMetadataBlockElementField.class);
			final var updated = getOffsetDateTimeFieldValue(updatedDateField);

			DatasetMetadataBlockElementField deletedDateField = objectMapper.convertValue(
					temporalSpreadValues.get(DATASET_DATES_DELETED.getName()), DatasetMetadataBlockElementField.class);
			final var deleted = getOffsetDateTimeFieldValue(deletedDateField);

			ReferenceDates datesDataset = new ReferenceDates().created(created).validated(validated)
					.published(published).updated(updated).deleted(deleted);

			metadata.setDatasetDates(datesDataset);
		}
	}

	private void addSynopsisField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		List<DictionaryEntry> dictionaryEntries = metadata.getSynopsis();
		if (!CollectionUtils.isEmpty(dictionaryEntries)) {
			List<Map<String, Object>> synospisValues = new ArrayList<>();
			for (DictionaryEntry dictionaryEntry : dictionaryEntries) {
				Map<String, Object> synospisValue = new HashMap<>();

				addOptionalPrimitiveField(dictionaryEntry.getLang().getValue(), synospisValue, SYNOPSIS_LANGUAGE);
				addOptionalPrimitiveField(dictionaryEntry.getText(), synospisValue, SYNOPSIS_TEXT);

				synospisValues.add(synospisValue);
			}

			DatasetMetadataBlockElementField synospisField = createField(SYNOPSIS, synospisValues);
			fields.add(synospisField);
		}
	}

	private void setSynopsis(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			List<DictionaryEntry> dictionnaryEntries = new ArrayList<>();
			List<?> synospisFieldValues = (List<?>) field.getValue();

			for (Object synospisFieldValueObject : synospisFieldValues) {
				HashMap<?, ?> synospisFieldValue = (HashMap<?, ?>) synospisFieldValueObject;

				DatasetMetadataBlockElementField synospisTextField = objectMapper
						.convertValue(synospisFieldValue.get(SYNOPSIS_TEXT.getName()), DatasetMetadataBlockElementField.class);
				String text = getPrimitiveFieldValue(synospisTextField);

				DatasetMetadataBlockElementField synospisLangField = objectMapper.convertValue(
						synospisFieldValue.get(SYNOPSIS_LANGUAGE.getName()), DatasetMetadataBlockElementField.class);
				String languageValue = synospisLangField.getValue().toString();
				Language language = Language.fromValue(languageValue);

				DictionaryEntry dictionaryEntry = new DictionaryEntry().text(text).lang(language);

				dictionnaryEntries.add(dictionaryEntry);
			}

			metadata.setSynopsis(dictionnaryEntries);
		}
	}

	private void addSummaryField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		List<DictionaryEntry> dictionaryEntries = metadata.getSummary();
		if (!CollectionUtils.isEmpty(dictionaryEntries)) {
			List<Map<String, Object>> summaryValues = new ArrayList<>();
			for (DictionaryEntry dictionaryEntry : dictionaryEntries) {
				Map<String, Object> summaryValue = new HashMap<>();

				addOptionalPrimitiveField(dictionaryEntry.getLang().getValue(), summaryValue, SUMMARY_LANGUAGE);
				addOptionalPrimitiveField(dictionaryEntry.getText(), summaryValue, SUMMARY_TEXT);

				summaryValues.add(summaryValue);
			}

			DatasetMetadataBlockElementField summaryField = createField(SUMMARY, summaryValues);
			fields.add(summaryField);
		}
	}

	private void setSummary(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {
			List<DictionaryEntry> dictionnaryEntries = new ArrayList<>();
			List<?> summaryFieldValues = (List<?>) field.getValue();
			for (Object summaryFieldValueObject : summaryFieldValues) {
				HashMap<?, ?> summaryFieldValue = (HashMap<?, ?>) summaryFieldValueObject;

				DatasetMetadataBlockElementField summaryTextField = objectMapper
						.convertValue(summaryFieldValue.get(SUMMARY_TEXT.getName()), DatasetMetadataBlockElementField.class);
				String text = getPrimitiveFieldValue(summaryTextField);

				DatasetMetadataBlockElementField summaryLangField = objectMapper
						.convertValue(summaryFieldValue.get(SUMMARY_LANGUAGE.getName()), DatasetMetadataBlockElementField.class);
				String languageValue = summaryLangField.getValue().toString();
				Language language = Language.fromValue(languageValue);

				DictionaryEntry dictionaryEntry = new DictionaryEntry().text(text).lang(language);

				dictionnaryEntries.add(dictionaryEntry);
			}

			metadata.setSummary(dictionnaryEntries);
		}
	}

	private void addContactsField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		List<Contact> contacts = metadata.getContacts();

		if (!CollectionUtils.isEmpty(contacts)) {
			List<Map<String, Object>> contactValues = new ArrayList<>();
			for (Contact contact : contacts) {
				Map<String, Object> contactValue = new HashMap<>();

				addOptionalPrimitiveField(contact.getContactId().toString(), contactValue, CONTACT_ID);
				addOptionalPrimitiveField(contact.getContactName(), contactValue, CONTACT_NAME);
				addOptionalPrimitiveField(contact.getEmail(), contactValue, CONTACT_EMAIL);
				addOptionalPrimitiveField(contact.getOrganizationName(), contactValue, CONTACT_ORGANIZATION_NAME);
				addOptionalPrimitiveField(contact.getRole(), contactValue, CONTACT_ROLE);

				contactValues.add(contactValue);
			}

			DatasetMetadataBlockElementField contactField = createField(CONTACT, contactValues);
			fields.add(contactField);
		}
	}

	private void setContacts(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			List<Contact> contacts = new ArrayList<>();
			List<?> contactFieldValues = (List<?>) field.getValue();

			for (Object contactFieldValueObject : contactFieldValues) {
				HashMap<?, ?> contactFieldValue = (HashMap<?, ?>) contactFieldValueObject;

				DatasetMetadataBlockElementField contactIdField = objectMapper
						.convertValue(contactFieldValue.get(CONTACT_ID.getName()), DatasetMetadataBlockElementField.class);
				String contactId = getPrimitiveFieldValue(contactIdField);

				DatasetMetadataBlockElementField contactOrganizationNameField = objectMapper.convertValue(
						contactFieldValue.get(CONTACT_ORGANIZATION_NAME.getName()), DatasetMetadataBlockElementField.class);
				String organizationName = getPrimitiveFieldValue(contactOrganizationNameField);

				DatasetMetadataBlockElementField contactNameField = objectMapper
						.convertValue(contactFieldValue.get(CONTACT_NAME.getName()), DatasetMetadataBlockElementField.class);
				String contactName = getPrimitiveFieldValue(contactNameField);

				DatasetMetadataBlockElementField roleField = objectMapper
						.convertValue(contactFieldValue.get(CONTACT_ROLE.getName()), DatasetMetadataBlockElementField.class);
				String role = getPrimitiveFieldValue(roleField);

				DatasetMetadataBlockElementField emailField = objectMapper
						.convertValue(contactFieldValue.get(CONTACT_EMAIL.getName()), DatasetMetadataBlockElementField.class);
				String email = getPrimitiveFieldValue(emailField);

				Contact contact = new Contact().contactId(UUID.fromString(contactId)).organizationName(organizationName)
						.contactName(contactName).role(role).email(email);

				contacts.add(contact);
			}

			metadata.setContacts(contacts);
		}
	}

	private void addAvailableFormatsField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		List<Media> medias = metadata.getAvailableFormats();

		if (!CollectionUtils.isEmpty(medias)) {
			List<Map<String, Object>> mediaValues = new ArrayList<>();

			for (Media media : medias) {
				Map<String, Object> mediaValue = new HashMap<>();

				// propriétés de Media
				addOptionalPrimitiveField(media.getMediaId().toString(), mediaValue, MEDIA_ID);
				addOptionalControlledField(media.getMediaType().getValue(), mediaValue, MEDIA_TYPE);

				// propriétés de Connector
				addMediaConnectorField(media.getConnector(), mediaValue);

				if (MediaTypeEnum.FILE.equals(media.getMediaType()) && media instanceof MediaFile) {
					// propriétés de MediaFile
					MediaFile file = (MediaFile) media;
					addMediaFileField(file, mediaValue);

				} else if (MediaTypeEnum.SERIES.equals(media.getMediaType()) && media instanceof MediaSeries) {
					// propriétés de MediaSeries
					MediaSeries series = (MediaSeries) media;
					addMediaSeriesField(series, mediaValue);
				}
				mediaValues.add(mediaValue);
			}

			// objet Media
			DatasetMetadataBlockElementField mediaField = createField(MEDIA, mediaValues);
			fields.add(mediaField);
		}
	}

	private void addMediaConnectorField(Connector connector, Map<String, Object> mediaValue) {
		if (connector != null) {
			// propriétés de Connector
			addOptionalPrimitiveField(connector.getUrl(), mediaValue, MEDIA_CONNECTOR_URL);
			addOptionalPrimitiveField(connector.getInterfaceContract(), mediaValue, MEDIA_CONNECTOR_INTERFACE_CONTRACT);
		}
	}

	private void addMediaFileField(MediaFile file, Map<String, Object> mediaValue) {

		// propriétés de File
		if (file.getFileStructure() != null) {
			addOptionalPrimitiveField(file.getFileStructure(), mediaValue, FILE_STRUCTURE);
		}
		addOptionalPrimitiveField(file.getFileSize(), mediaValue, FILE_SIZE);
		addOptionalPrimitiveField(file.getFileType().getValue(), mediaValue, FILE_TYPE);
		addOptionalPrimitiveField(file.getFileEncoding(), mediaValue, FILE_ENCODING);

		// propriétés de Checksum
		addFileChecksumField(file, mediaValue);
	}

	private void addFileChecksumField(MediaFile file, Map<String, Object> mediaValue) {
		Objects.requireNonNull(file.getChecksum(), MessageUtils.buildErrorMessageRequiredMandatoryAttributes(FILE_CHECKSUM_ALGO));
		Objects.requireNonNull(file.getChecksum().getAlgo(), MessageUtils.buildErrorMessageRequiredMandatoryAttributes(FILE_CHECKSUM_ALGO));

		addMandatoryPrimitiveField(file.getChecksum().getAlgo().getValue(), mediaValue, FILE_CHECKSUM_ALGO);
		addOptionalPrimitiveField(file.getChecksum().getHash(), mediaValue, FILE_CHECKSUM_HASH);
	}

	private void addMediaSeriesField(MediaSeries series, Map<String, Object> mediaValue) {

		addOptionalPrimitiveField(series.getLatency(), mediaValue, SERIES_LATENCY);
		addOptionalPrimitiveField(series.getPeriod(), mediaValue, SERIES_PERIOD);
		addOptionalPrimitiveField(series.getCurrentNumberOfRecords(), mediaValue, SERIES_CURENT_NUMBER_OF_RECORDS);
		addOptionalPrimitiveField(series.getCurrentSize(), mediaValue, SERIES_CURENT_SIZE);
		addOptionalPrimitiveField(series.getTotalNumberOfRecords(), mediaValue, SERIES_TOTAL_NUMBER_OF_RECORDS);
		addOptionalPrimitiveField(series.getTotalSize(), mediaValue, SERIES_TOTAL_SIZE);
	}

	private void setAvailableFormats(Metadata metadata, DatasetMetadataBlockElementField field) {

		if (field != null && field.getValue() != null) {

			List<Media> medias = new ArrayList<>();
			List<?> mediaFieldValues = (List<?>) field.getValue();

			for (Object mediaFieldValueObject : mediaFieldValues) {
				HashMap<?, ?> mediaFieldMap = (HashMap<?, ?>) mediaFieldValueObject;

				// mediaId
				DatasetMetadataBlockElementField mediaIdField = objectMapper.convertValue(mediaFieldMap.get(MEDIA_ID.getName()),
						DatasetMetadataBlockElementField.class);
				String mediaId = getPrimitiveFieldValue(mediaIdField);

				// mediaType
				DatasetMetadataBlockElementField mediaTypeField = objectMapper
						.convertValue(mediaFieldMap.get(MEDIA_TYPE.getName()), DatasetMetadataBlockElementField.class);
				Object mediaTypeValue = mediaTypeField.getValue();
				MediaTypeEnum mediaType = (mediaTypeValue == null) ? null
						: MediaTypeEnum.fromValue(mediaTypeValue.toString());

				// Connector
				Connector connector = getMediaConnector(mediaFieldMap);

				// File ou MediaSeries
				if (MediaTypeEnum.FILE.equals(mediaType)) {

					MediaFile file = getMediaFileFormat(mediaFieldMap);
					file.mediaId(UUID.fromString(mediaId)).mediaType(mediaType).connector(connector);
					medias.add(file);

				} else if (MediaTypeEnum.SERIES.equals(mediaType)) {

					MediaSeries series = getMediaSeries(mediaFieldMap);
					series.mediaId(UUID.fromString(mediaId)).mediaType(mediaType).connector(connector);
					medias.add(series);
				}
			}
			metadata.setAvailableFormats(medias);
		}
	}

	private MediaSeries getMediaSeries(HashMap<?, ?> mediaFieldMap) {

		// latency
		DatasetMetadataBlockElementField latencyField = objectMapper.convertValue(mediaFieldMap.get(SERIES_LATENCY.getName()),
				DatasetMetadataBlockElementField.class);
		Integer latency = getIntegerFieldValue(latencyField);

		// period
		DatasetMetadataBlockElementField periodField = objectMapper.convertValue(mediaFieldMap.get(SERIES_PERIOD.getName()),
				DatasetMetadataBlockElementField.class);
		Integer period = getIntegerFieldValue(periodField);

		// current_number_of_records
		DatasetMetadataBlockElementField nbRecordsField = objectMapper.convertValue(
				mediaFieldMap.get(SERIES_CURENT_NUMBER_OF_RECORDS.getName()), DatasetMetadataBlockElementField.class);
		Integer currentNbRecords = getIntegerFieldValue(nbRecordsField);

		// current_size
		DatasetMetadataBlockElementField currentSizeField = objectMapper
				.convertValue(mediaFieldMap.get(SERIES_CURENT_SIZE.getName()), DatasetMetadataBlockElementField.class);
		Integer currentSize = getIntegerFieldValue(currentSizeField);

		// total_number_of_records
		DatasetMetadataBlockElementField totalNbRecordsField = objectMapper.convertValue(
				mediaFieldMap.get(SERIES_TOTAL_NUMBER_OF_RECORDS.getName()), DatasetMetadataBlockElementField.class);
		Integer totalNbRecords = getIntegerFieldValue(totalNbRecordsField);

		// total_size
		DatasetMetadataBlockElementField totalSizeField = objectMapper
				.convertValue(mediaFieldMap.get(SERIES_TOTAL_SIZE.getName()), DatasetMetadataBlockElementField.class);
		Integer totalSize = getIntegerFieldValue(totalSizeField);

		return new MediaSeries().latency(latency).period(period).currentNumberOfRecords(currentNbRecords)
				.currentSize(currentSize).totalNumberOfRecords(totalNbRecords).totalSize(totalSize);
	}

	private MediaFile getMediaFileFormat(HashMap<?, ?> mediaFieldMap) {

		// fileStructure
		DatasetMetadataBlockElementField structureField = objectMapper.convertValue(mediaFieldMap.get(FILE_STRUCTURE.getName()),
				DatasetMetadataBlockElementField.class);
		String fileStructureValue = getPrimitiveFieldValue(structureField);

		// fileSize
		DatasetMetadataBlockElementField sizeField = objectMapper.convertValue(mediaFieldMap.get(FILE_SIZE.getName()),
				DatasetMetadataBlockElementField.class);
		Integer fileSize = getIntegerFieldValue(sizeField);

		// fileType
		DatasetMetadataBlockElementField fileTypeField = objectMapper.convertValue(mediaFieldMap.get(FILE_TYPE.getName()),
				DatasetMetadataBlockElementField.class);
		Object fileTypeValue = fileTypeField.getValue();
		MediaType fileType = (fileTypeValue == null) ? null : MediaType.fromValue(fileTypeValue.toString());

		// fileEncoding
		DatasetMetadataBlockElementField fileEncodingField = objectMapper.convertValue(mediaFieldMap.get(FILE_ENCODING.getName()),
				DatasetMetadataBlockElementField.class);
		String fileEncoding = getPrimitiveFieldValue(fileEncodingField);

		// Checksum
		MediaFileAllOfChecksum checksum = getFileChecksum(mediaFieldMap);

		return new MediaFile().fileStructure(fileStructureValue).fileSize(fileSize).fileType(fileType)
				.fileEncoding(fileEncoding).checksum(checksum);
	}

	private MediaFileAllOfChecksum getFileChecksum(HashMap<?, ?> mediaFieldMap) {

		// algo
		DatasetMetadataBlockElementField algoField = objectMapper.convertValue(mediaFieldMap.get(FILE_CHECKSUM_ALGO.getName()),
				DatasetMetadataBlockElementField.class);
		Object algoValue = algoField.getValue();
		HashAlgorithm algo = (algoValue == null) ? null : HashAlgorithm.fromValue(algoValue.toString());

		// hash
		DatasetMetadataBlockElementField hashField = objectMapper.convertValue(mediaFieldMap.get(FILE_CHECKSUM_HASH.getName()),
				DatasetMetadataBlockElementField.class);
		String hash = getPrimitiveFieldValue(hashField);

		return new MediaFileAllOfChecksum().algo(algo).hash(hash);
	}

	private Connector getMediaConnector(HashMap<?, ?> mediaFieldMap) {

		// connectorUrl
		DatasetMetadataBlockElementField urlField = objectMapper.convertValue(mediaFieldMap.get(MEDIA_CONNECTOR_URL.getName()),
				DatasetMetadataBlockElementField.class);
		String connectorUrl = getPrimitiveFieldValue(urlField);

		// interfaceContract
		DatasetMetadataBlockElementField interfaceContractField = objectMapper.convertValue(
				mediaFieldMap.get(MEDIA_CONNECTOR_INTERFACE_CONTRACT.getName()), DatasetMetadataBlockElementField.class);
		String interfaceContract = getPrimitiveFieldValue(interfaceContractField);

		return new Connector().interfaceContract(interfaceContract).url(connectorUrl);
	}

	private void setResourceLanguages(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			List<?> resourceLanguageFieldValues = (List<?>) field.getValue();
			List<Language> languages = new ArrayList<>();

			for (Object languageValue : resourceLanguageFieldValues) {

				Language language = Language.fromValue(languageValue.toString());
				languages.add(language);
			}
			metadata.setResourceLanguages(languages);
		}
	}

	private void addResourceLanguagesField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {

		List<Language> ressourceLanguages = metadata.getResourceLanguages();

		if (!CollectionUtils.isEmpty(ressourceLanguages)) {
			List<String> languageValues = ressourceLanguages.stream().map(Language::getValue)
					.collect(Collectors.toList());

			DatasetMetadataBlockElementField keywordField = createField(RESOURCE_LANGUAGES, languageValues);
			fields.add(keywordField);
		}
	}

	private void addProducerField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		Organization producer = metadata.getProducer();
		Objects.requireNonNull(producer, MessageUtils.buildErrorMessageRequiredMandatoryAttributes(PRODUCER));

		Map<String, Object> producerValue = new HashMap<>();

		addOptionalPrimitiveField(producer.getOrganizationId().toString(), producerValue, PRODUCER_ORGANIZATION_ID);
		addOptionalPrimitiveField(producer.getOrganizationName(), producerValue, PRODUCER_ORGANIZATION_NAME);
		addOptionalPrimitiveField(producer.getOrganizationAddress(), producerValue, PRODUCER_ORGANIZATION_ADDRESS);

		DatasetMetadataBlockElementField producerField = createField(PRODUCER, producerValue);
		fields.add(producerField);
	}

	private void setProducer(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			HashMap<?, ?> producerValues = (HashMap<?, ?>) field.getValue();

			// organizationId
			DatasetMetadataBlockElementField producerOrganizationIdField = objectMapper
					.convertValue(producerValues.get(PRODUCER_ORGANIZATION_ID.getName()), DatasetMetadataBlockElementField.class);
			String organizationId = getPrimitiveFieldValue(producerOrganizationIdField);

			// organizationName
			DatasetMetadataBlockElementField producerOrganizationNameField = objectMapper.convertValue(
					producerValues.get(PRODUCER_ORGANIZATION_NAME.getName()), DatasetMetadataBlockElementField.class);
			String organizationName = getPrimitiveFieldValue(producerOrganizationNameField);

			// address
			DatasetMetadataBlockElementField addressField = objectMapper.convertValue(
					producerValues.get(PRODUCER_ORGANIZATION_ADDRESS.getName()), DatasetMetadataBlockElementField.class);
			String address = getPrimitiveFieldValue(addressField);

			Organization producer = new Organization().organizationId(UUID.fromString(organizationId))
					.organizationName(organizationName).organizationAddress(address);

			metadata.setProducer(producer);
		}
	}

	private void addDatasetSizeField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		DatasetSize datasetSize = metadata.getDatasetSize();

		if (datasetSize != null) {

			Map<String, Object> datasetSizeValue = new HashMap<>();

			addOptionalPrimitiveField(datasetSize.getNumberOfFields(), datasetSizeValue, DATASET_SIZE_NUMBER_OF_FIELDS);
			addOptionalPrimitiveField(datasetSize.getNumbersOfRecords(), datasetSizeValue,
					DATASET_SIZE_NUMBER_OF_RECORDS);

			DatasetMetadataBlockElementField datasetSizeField = createField(DATASET_SIZE, datasetSizeValue);
			fields.add(datasetSizeField);
		}
	}

	private void setDatasetSize(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			HashMap<?, ?> datasetSizeValues = (HashMap<?, ?>) field.getValue();

			// numberOfFields
			DatasetMetadataBlockElementField numberOfFieldField = objectMapper.convertValue(
					datasetSizeValues.get(DATASET_SIZE_NUMBER_OF_FIELDS.getName()), DatasetMetadataBlockElementField.class);
			Integer numberOfFields = getIntegerFieldValue(numberOfFieldField);

			// numberOfRecords
			DatasetMetadataBlockElementField numberOfRecordsField = objectMapper.convertValue(
					datasetSizeValues.get(DATASET_SIZE_NUMBER_OF_RECORDS.getName()), DatasetMetadataBlockElementField.class);
			Integer numberOfRecords = getIntegerFieldValue(numberOfRecordsField);

			DatasetSize datasetSize = new DatasetSize().numberOfFields(numberOfFields)
					.numbersOfRecords(numberOfRecords);
			metadata.setDatasetSize(datasetSize);
		}
	}

	private void setStorageStatus(Metadata metadata, DatasetMetadataBlockElementField field) {

		DatasetMetadataBlockElementField storageStatusField = objectMapper.convertValue(field,
				DatasetMetadataBlockElementField.class);

		Object storageStatusValue = storageStatusField.getValue();
		StorageStatusEnum storageStatus = (storageStatusValue == null) ? null
				: StorageStatusEnum.fromValue(storageStatusValue.toString());

		metadata.setStorageStatus(storageStatus);
	}

	private void addStorageStatusField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {

		StorageStatusEnum storageStatus = metadata.getStorageStatus();
		Objects.requireNonNull(storageStatus, MessageUtils.buildErrorMessageRequiredMandatoryAttributes(STORAGE_STATUS));

		DatasetMetadataBlockElementField storageStatusField = createField(STORAGE_STATUS, storageStatus.getValue());
		fields.add(storageStatusField);

	}

	private void addMetadataInfoDatesField(@Valid ReferenceDates metadataInfoDates,
			Map<String, Object> metadataInfoValue) {
		if (metadataInfoDates != null) {

			addMandatoryDateTimeField(metadataInfoDates.getCreated(), metadataInfoValue, METADATA_INFO_DATES_CREATED);
			addOptionalDateTimeField(metadataInfoDates.getValidated(), metadataInfoValue,
					METADATA_INFO_DATES_VALIDATED);
			addOptionalDateTimeField(metadataInfoDates.getPublished(), metadataInfoValue,
					METADATA_INFO_DATES_PUBLISHED);
			addMandatoryDateTimeField(metadataInfoDates.getUpdated(), metadataInfoValue, METADATA_INFO_DATES_UPDATED);
			addOptionalDateTimeField(metadataInfoDates.getDeleted(), metadataInfoValue, METADATA_INFO_DATES_DELETED);
		}
	}

	private void addMetadataInfoProviderField(@Valid Organization provider, Map<String, Object> metadataInfoValue) {

		if (provider != null) {
			addOptionalPrimitiveField(provider.getOrganizationId().toString(), metadataInfoValue,
					METADATA_INFO_PROVIDER_ORGANIZATION_ID);
			addOptionalPrimitiveField(provider.getOrganizationName(), metadataInfoValue,
					METADATA_INFO_PROVIDER_ORGANIZATION_NAME);
			addOptionalPrimitiveField(provider.getOrganizationAddress(), metadataInfoValue,
					METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS);
		}
	}

	private void addMetadataInfoContactField(@Valid List<Contact> metadataContacts,
			List<DatasetMetadataBlockElementField> fields) {

		if (!CollectionUtils.isEmpty(metadataContacts)) {
			List<Map<String, Object>> contactValues = new ArrayList<>();

			for (Contact contact : metadataContacts) {
				Map<String, Object> contactValue = new HashMap<>();

				addOptionalPrimitiveField(contact.getContactId().toString(), contactValue, METADATA_INFO_CONTACT_ID);
				addOptionalPrimitiveField(contact.getContactName(), contactValue, METADATA_INFO_CONTACT_NAME);
				addOptionalPrimitiveField(contact.getEmail(), contactValue, METADATA_INFO_CONTACT_EMAIL);
				addOptionalPrimitiveField(contact.getOrganizationName(), contactValue,
						METADATA_INFO_CONTACT_ORGANIZATION_NAME);
				addOptionalPrimitiveField(contact.getRole(), contactValue, METADATA_INFO_CONTACT_ROLE);

				contactValues.add(contactValue);
			}

			DatasetMetadataBlockElementField contactField = createField(METADATA_INFO_CONTACT, contactValues);
			fields.add(contactField);
		}

	}

	private void addMetadataInfoField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {

		MetadataMetadataInfo metadataInfo = metadata.getMetadataInfo();
		Objects.requireNonNull(metadataInfo, MessageUtils.buildErrorMessageRequiredMandatoryAttributes(METADATA_INFO));

		Map<String, Object> metadataInfoValue = new HashMap<>();

		// apiVersion
		addOptionalPrimitiveField(metadataInfo.getApiVersion(), metadataInfoValue, METADATA_INFO_API_VERSION);

		// ReferenceDates
		addMetadataInfoDatesField(metadataInfo.getMetadataDates(), metadataInfoValue);

		// Provider (objet Organization)
		addMetadataInfoProviderField(metadataInfo.getMetadataProvider(), metadataInfoValue);

		// contacts
		addMetadataInfoContactField(metadataInfo.getMetadataContacts(), fields);

		DatasetMetadataBlockElementField metadataInfoField = createField(METADATA_INFO, metadataInfoValue);
		fields.add(metadataInfoField);
	}

	private ReferenceDates buildMetadataInfoDates(HashMap<?, ?> metadataInfoValues) {

		DatasetMetadataBlockElementField createdDateField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_DATES_CREATED.getName()), DatasetMetadataBlockElementField.class);
		final var created = getOffsetDateTimeFieldValue(createdDateField);

		DatasetMetadataBlockElementField validatedDateField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_DATES_VALIDATED.getName()), DatasetMetadataBlockElementField.class);
		final var validated = getOffsetDateTimeFieldValue(validatedDateField);

		DatasetMetadataBlockElementField publishedDateField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_DATES_PUBLISHED.getName()), DatasetMetadataBlockElementField.class);
		final var published = getOffsetDateTimeFieldValue(publishedDateField);

		DatasetMetadataBlockElementField updatedDateField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_DATES_UPDATED.getName()), DatasetMetadataBlockElementField.class);
		final var updated = getOffsetDateTimeFieldValue(updatedDateField);

		DatasetMetadataBlockElementField deletedDateField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_DATES_DELETED.getName()), DatasetMetadataBlockElementField.class);
		final var deleted = getOffsetDateTimeFieldValue(deletedDateField);

		return new ReferenceDates().created(created).validated(validated).published(published).updated(updated)
				.deleted(deleted);
	}

	private Organization buildMetadataInfoProvider(Map<?, ?> metadataInfoValues) {

		DatasetMetadataBlockElementField producerOrganizationIdField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_PROVIDER_ORGANIZATION_ID.getName()), DatasetMetadataBlockElementField.class);
		String organizationId = getPrimitiveFieldValue(producerOrganizationIdField);

		DatasetMetadataBlockElementField producerOrganizationNameField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_PROVIDER_ORGANIZATION_NAME.getName()),
				DatasetMetadataBlockElementField.class);
		String organizationName = getPrimitiveFieldValue(producerOrganizationNameField);

		DatasetMetadataBlockElementField addressField = objectMapper.convertValue(
				metadataInfoValues.get(METADATA_INFO_PROVIDER_ORGANIZATION_ADDRESS.getName()),
				DatasetMetadataBlockElementField.class);
		String address = getPrimitiveFieldValue(addressField);

		Organization organization = null;

		if (StringUtils.isNotEmpty(organizationName) || StringUtils.isNotEmpty(address) || StringUtils.isNotEmpty(organizationId)) {
			organization = new Organization()
					.organizationName(organizationName)
					.organizationAddress(address);

			if (StringUtils.isNotEmpty(organizationId)) {
				organization.setOrganizationId(UUID.fromString(organizationId));
			}
		}

		return organization;
	}

	private List<Contact> buildMetadataInfoContacts(DatasetMetadataBlockElementField contactField) {

		List<Contact> contacts = null;

		if (contactField != null && contactField.getValue() != null) {

			List<?> contactFieldValues = (List<?>) contactField.getValue();
			contacts = new ArrayList<>();

			for (Object contactFieldValueObject : contactFieldValues) {
				HashMap<?, ?> contactFieldValue = (HashMap<?, ?>) contactFieldValueObject;

				DatasetMetadataBlockElementField contactIdField = objectMapper.convertValue(
						contactFieldValue.get(METADATA_INFO_CONTACT_ID.getName()), DatasetMetadataBlockElementField.class);
				String contactId = getPrimitiveFieldValue(contactIdField);

				DatasetMetadataBlockElementField contactOrganizationNameField = objectMapper.convertValue(
						contactFieldValue.get(METADATA_INFO_CONTACT_ORGANIZATION_NAME.getName()),
						DatasetMetadataBlockElementField.class);
				String organizationName = getPrimitiveFieldValue(contactOrganizationNameField);

				DatasetMetadataBlockElementField contactNameField = objectMapper.convertValue(
						contactFieldValue.get(METADATA_INFO_CONTACT_NAME.getName()), DatasetMetadataBlockElementField.class);
				String contactName = getPrimitiveFieldValue(contactNameField);

				DatasetMetadataBlockElementField roleField = objectMapper.convertValue(
						contactFieldValue.get(METADATA_INFO_CONTACT_ROLE.getName()), DatasetMetadataBlockElementField.class);
				String role = getPrimitiveFieldValue(roleField);

				DatasetMetadataBlockElementField emailField = objectMapper.convertValue(
						contactFieldValue.get(METADATA_INFO_CONTACT_EMAIL.getName()), DatasetMetadataBlockElementField.class);
				String email = getPrimitiveFieldValue(emailField);

				Contact contact = new Contact().contactId(UUID.fromString(contactId)).organizationName(organizationName)
						.contactName(contactName).role(role).email(email);

				contacts.add(contact);
			}
		}
		return contacts;
	}

	private void setMetadataInfo(Metadata metadata, List<DatasetMetadataBlockElementField> rudiFields,
			DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			HashMap<?, ?> metadataInfoValues = (HashMap<?, ?>) field.getValue();

			// apiVersion
			DatasetMetadataBlockElementField apiVersionField = objectMapper.convertValue(
					metadataInfoValues.get(METADATA_INFO_API_VERSION.getName()), DatasetMetadataBlockElementField.class);
			String apiVersion = getPrimitiveFieldValue(apiVersionField);

			// ReferenceDates
			ReferenceDates metadataInfoDates = buildMetadataInfoDates(metadataInfoValues);

			// Provider
			Organization provider = buildMetadataInfoProvider(metadataInfoValues);

			// liste des contacts de MetadataInfo
			// Attention : dans le dataverse, les contacts de MetadataInfo se trouvent un niveau au-dessus
			// et non dans MetadataInfo, cause limitation du dataverse
			DatasetMetadataBlockElementField metadataInfoContactField = getField(rudiFields, METADATA_INFO_CONTACT);

			List<Contact> contacts = buildMetadataInfoContacts(metadataInfoContactField);

			MetadataMetadataInfo metadataInfo = new MetadataMetadataInfo().apiVersion(apiVersion)
					.metadataDates(metadataInfoDates).metadataProvider(provider).metadataContacts(contacts);

			metadata.setMetadataInfo(metadataInfo);
		}

	}

}
