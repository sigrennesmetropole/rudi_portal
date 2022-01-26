package org.rudi.facet.kmedia.helper.dataset.metadatablock.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.AbstractMetadataBlockElementMapper;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.bean.MediaDataset;
import org.rudi.facet.kmedia.bean.MediaOrigin;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR_AFFILIATION;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR_IDENTIFIER;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR_NAME;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.CONTACT;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.CONTACT_EMAIL;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.DESCRIPTION;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.DESCRIPTION_DATE;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.DESCRIPTION_VALUE;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.SUBJECT;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.TITLE;
import static org.rudi.facet.kmedia.constant.CitationMediaField.KIND_OF_DATA;

@Component
public class MediaDatasetCitationBlockMapper extends AbstractMetadataBlockElementMapper<MediaDataset> {

	/*
	 * Display name
	 */
	private static final String CITATION_DISPLAY_NAME = "Citation Metadata";

	/*
	 * Default values
	 */
	private static final String DEFAULT_SUBJECT = "Other";
	private static final String DEFAULT_CONTACT_EMAIL = "dataverse@rudi.com";

	private final ObjectMapper objectMapper = new ObjectMapper();

	public MediaDatasetCitationBlockMapper(FieldGenerator fieldGenerator) {
		super(fieldGenerator);
	}

	@Override
	public void datasetMetadataBlockElementToData(DatasetMetadataBlockElement datasetMetadataBlockElement,
			MediaDataset mediaDataset) {
		List<DatasetMetadataBlockElementField> citationFields = datasetMetadataBlockElement.getFields();

		setTitle(mediaDataset, getField(citationFields, TITLE));
		setAuthor(mediaDataset, getField(citationFields, AUTHOR));
		setKindOfData(mediaDataset, getField(citationFields, KIND_OF_DATA));

	}

	@Override
	public String getDisplayName() {
		return CITATION_DISPLAY_NAME;
	}

	@Override
	public List<DatasetMetadataBlockElementField> createFields(MediaDataset mediaDataset) {
		List<DatasetMetadataBlockElementField> fields = new ArrayList<>();

		addTitleField(mediaDataset, fields);
		addAuthors(mediaDataset, fields);
		addDescription(mediaDataset, fields);
		addKinfOfDataField(mediaDataset, fields);

		// valeurs par défaut
		addContacts(fields);
		addSubjectsField(fields);

		return fields;
	}

	private void addKinfOfDataField(MediaDataset mediaDataset, List<DatasetMetadataBlockElementField> fields) {
		List<String> kindOfDataValues = new ArrayList<>();

		String kindOfData = mediaDataset.getKindOfData().getValue();
		kindOfDataValues.add(kindOfData);
		DatasetMetadataBlockElementField kindOfDataField = createField(KIND_OF_DATA, kindOfDataValues);
		fields.add(kindOfDataField);
	}

	private void addTitleField(MediaDataset mediaDataset, List<DatasetMetadataBlockElementField> fields) {
		DatasetMetadataBlockElementField titleField = createField(TITLE, mediaDataset.getTitle());
		fields.add(titleField);
	}

	private void addSubjectsField(List<DatasetMetadataBlockElementField> fields) {
		DatasetMetadataBlockElementField subjectField = createField(SUBJECT, Collections.singletonList(DEFAULT_SUBJECT));
		fields.add(subjectField);
	}

	private void addAuthors(MediaDataset mediaDataset, List<DatasetMetadataBlockElementField> fields) {

		List<Map<String, Object>> summaryValues = new ArrayList<>();

		// Création d'un unique auteur
		Map<String, Object> summaryValue = new HashMap<>();

		DatasetMetadataBlockElementField authorIdentifierField = createField(AUTHOR_IDENTIFIER, mediaDataset.getAuthorIdentifier().toString());
		summaryValue.put(AUTHOR_IDENTIFIER.getName(), authorIdentifierField);

		DatasetMetadataBlockElementField authorNameField = createField(AUTHOR_NAME, mediaDataset.getAuthorName());
		summaryValue.put(AUTHOR_NAME.getName(), authorNameField);

		DatasetMetadataBlockElementField authorAffiliationField = createField(AUTHOR_AFFILIATION, mediaDataset.getAuthorAffiliation().getValue());
		summaryValue.put(AUTHOR_AFFILIATION.getName(), authorAffiliationField);

		summaryValues.add(summaryValue);

		DatasetMetadataBlockElementField authorField = createField(AUTHOR, summaryValues);
		fields.add(authorField);
	}

	private void addContacts(List<DatasetMetadataBlockElementField> fields) {

		List<Map<String, Object>> contactValues = new ArrayList<>();

		// Création d'un unique contact (car obligatoire dans le dataverse)
		// avec les valeurs par défaut
		Map<String, Object> contactValue = new HashMap<>();

		DatasetMetadataBlockElementField contactNameField = createField(CONTACT_EMAIL, DEFAULT_CONTACT_EMAIL);
		contactValue.put(CONTACT_EMAIL.getName(), contactNameField);

		contactValues.add(contactValue);

		DatasetMetadataBlockElementField contactField = createField(CONTACT, contactValues);
		fields.add(contactField);
	}

	private void addDescription(MediaDataset mediaDataset, List<DatasetMetadataBlockElementField> fields) {

		String title = mediaDataset.getTitle();
		String descriptionText = !StringUtils.isEmpty(title) ? title : mediaDataset.getAuthorAffiliation().getValue();

		List<Map<String, Object>> descriptionValues = new ArrayList<>();

		// Création d'une unique description
		Map<String, Object> descriptionValue = new HashMap<>();

		DatasetMetadataBlockElementField descriptionValueField = createField(DESCRIPTION_VALUE, descriptionText);
		descriptionValue.put(DESCRIPTION_VALUE.getName(), descriptionValueField);

		DatasetMetadataBlockElementField descriptionDateField = createField(DESCRIPTION_DATE, LocalDate.now().toString());
		descriptionValue.put(DESCRIPTION_DATE.getName(), descriptionDateField);

		descriptionValues.add(descriptionValue);

		DatasetMetadataBlockElementField descriptionField = createField(DESCRIPTION, descriptionValues);
		fields.add(descriptionField);
	}

	private void setTitle(MediaDataset mediaDataset, DatasetMetadataBlockElementField field) {
		String title = getPrimitiveFieldValue(field);
		mediaDataset.setTitle(title);
	}

	private void setAuthor(MediaDataset mediaDataset, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			List<?> authorFieldValues = (List<?>) field.getValue();

			for (Object authorFieldValueObject : authorFieldValues) {
				HashMap<?, ?> authorFieldValue = (HashMap<?, ?>) authorFieldValueObject;

				DatasetMetadataBlockElementField authorIdField = objectMapper
						.convertValue(authorFieldValue.get(AUTHOR_IDENTIFIER.getName()), DatasetMetadataBlockElementField.class);
				String authorId = getPrimitiveFieldValue(authorIdField);

				DatasetMetadataBlockElementField authorAffiliationField = objectMapper
						.convertValue(authorFieldValue.get(AUTHOR_AFFILIATION.getName()), DatasetMetadataBlockElementField.class);
				Object authorAffiliationValue = authorAffiliationField.getValue();
				MediaOrigin mediaOrigin = (authorAffiliationValue == null) ? null
						: MediaOrigin.fromValue(authorAffiliationValue.toString());

				DatasetMetadataBlockElementField authorNameField = objectMapper
						.convertValue(authorFieldValue.get(AUTHOR_NAME.getName()), DatasetMetadataBlockElementField.class);
				String authorName = getPrimitiveFieldValue(authorNameField);

				if (authorId != null) {
					mediaDataset.setAuthorIdentifier(UUID.fromString(authorId));
				}
				mediaDataset.setAuthorAffiliation(mediaOrigin);
				mediaDataset.setAuthorName(authorName);
			}
		}
	}

	private void setKindOfData(MediaDataset mediaDataset, DatasetMetadataBlockElementField field) {
		if (field != null && field.getValue() != null) {

			List<?> kindOfDataFieldValues = (List<?>) field.getValue();
			if (!kindOfDataFieldValues.isEmpty()) {
				Object kindOfDataValue = kindOfDataFieldValues.get(0);
				KindOfData kindOfData = (kindOfDataValue == null) ? null
						: KindOfData.fromValue(kindOfDataValue.toString());

				mediaDataset.setKindOfData(kindOfData);
			}
		}
	}
}
