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
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.Metadata.StorageStatusEnum;
import org.rudi.facet.kaccess.bean.MetadataDatasetSize;
import org.rudi.facet.kaccess.bean.MetadataTemporalSpread;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields.AbstractFieldsMapper;
import org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields.RootFields;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_SIZE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_SIZE_NUMBER_OF_FIELDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DATASET_SIZE_NUMBER_OF_RECORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.DOI;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.GLOBAL_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.KEYWORDS;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.LOCAL_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESOURCE_LANGUAGES;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESOURCE_TITLE;
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

	private final Collection<AbstractFieldsMapper<?>> sortedFieldsMappers;

	public RudiMetadataBlockMapper(FieldGenerator fieldGenerator, DateTimeMapper dateTimeMapper, Collection<AbstractFieldsMapper<?>> fieldsMappers) {
		super(fieldGenerator, dateTimeMapper);
		this.sortedFieldsMappers = fieldsMappers.stream()
				.sorted(Comparator.comparingInt(AbstractFieldsMapper::getRank))
				.collect(Collectors.toList());
	}

	@Override
	public void datasetMetadataBlockElementToData(DatasetMetadataBlockElement datasetMetadataBlockElement,
			Metadata metadataParameter) throws DataverseMappingException {
		final var metadata = ObjectUtils.firstNonNull(metadataParameter, new Metadata());

		List<DatasetMetadataBlockElementField> rudiFields = datasetMetadataBlockElement.getFields();

		setGlobalId(metadata, getField(rudiFields, GLOBAL_ID));
		setLocalId(metadata, getField(rudiFields, LOCAL_ID));
		setDoiField(metadata, getField(rudiFields, DOI));
		setResourceTitle(metadata, getField(rudiFields, RESOURCE_TITLE));
		setSynopsis(metadata, getField(rudiFields, SYNOPSIS));
		setSummary(metadata, getField(rudiFields, SUMMARY));
		setTheme(metadata, getField(rudiFields, THEME));
		setKeywords(metadata, getField(rudiFields, KEYWORDS));
		setResourceLanguages(metadata, getField(rudiFields, RESOURCE_LANGUAGES));
		setTemporalSpread(metadata, getField(rudiFields, TEMPORAL_SPREAD));
		setDatasetSize(metadata, getField(rudiFields, DATASET_SIZE));
		setStorageStatus(metadata, getField(rudiFields, STORAGE_STATUS));

		final var allRudiRootFields = new RootFields(rudiFields);
		for (final var fieldsMapper : sortedFieldsMappers) {
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
		addResourceLanguagesField(metadata, fields);
		addTemporalSpreadField(metadata, fields);
		addDatasetSizeField(metadata, fields);
		addStorageStatusField(metadata, fields);

		for (final var fieldsMapper : sortedFieldsMappers) {
			fieldsMapper.metadataToFields(metadata, fields);
		}

		return fields;
	}

	private void addGlobalIdField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		Objects.requireNonNull(metadata.getGlobalId(), MessageUtils.buildErrorMessageRequiredMandatoryAttributes(GLOBAL_ID));
		final var id = metadata.getGlobalId().toString();
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
				final var languageValue = synospisLangField.getValue().toString();
				final var language = Language.fromValue(languageValue);

				final var dictionaryEntry = new DictionaryEntry().text(text).lang(language);

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
				final var languageValue = summaryLangField.getValue().toString();
				final var language = Language.fromValue(languageValue);

				final var dictionaryEntry = new DictionaryEntry().text(text).lang(language);

				dictionnaryEntries.add(dictionaryEntry);
			}

			metadata.setSummary(dictionnaryEntries);
		}
	}

	private void setResourceLanguages(Metadata metadata, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			List<?> resourceLanguageFieldValues = (List<?>) field.getValue();
			List<Language> languages = new ArrayList<>();

			for (Object languageValue : resourceLanguageFieldValues) {

				final var language = Language.fromValue(languageValue.toString());
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

	private void addDatasetSizeField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		MetadataDatasetSize datasetSize = metadata.getDatasetSize();

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
			final var numberOfFields = getIntegerFieldValue(numberOfFieldField);

			// numberOfRecords
			DatasetMetadataBlockElementField numberOfRecordsField = objectMapper.convertValue(
					datasetSizeValues.get(DATASET_SIZE_NUMBER_OF_RECORDS.getName()), DatasetMetadataBlockElementField.class);
			final var numberOfRecords = getIntegerFieldValue(numberOfRecordsField);

			MetadataDatasetSize datasetSize = new MetadataDatasetSize().numberOfFields(numberOfFields)
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

}
