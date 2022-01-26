package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.AbstractMetadataBlockElementMapper;
import org.rudi.facet.kaccess.bean.Contact;
import org.rudi.facet.kaccess.bean.DictionaryEntry;
import org.rudi.facet.kaccess.bean.Language;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.Organization;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR_AFFILIATION;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.AUTHOR_NAME;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.CONTACT;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.CONTACT_AFFILIATION;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.CONTACT_EMAIL;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.CONTACT_NAME;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.DESCRIPTION;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.DESCRIPTION_DATE;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.DESCRIPTION_VALUE;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.SUBJECT;
import static org.rudi.facet.dataverse.constant.CitationMetadataField.TITLE;

@Component
public class CitationMetadataBlockMapper extends AbstractMetadataBlockElementMapper<Metadata> {

	/*
	 * Display name
	 */
	public static final String CITATION_DISPLAY_NAME = "Citation Metadata";

	/*
	 * Default values
	 */
	private static final String DEFAULT_SUBJECT = "Other";
	private static final String DEFAULT_CONTACT_EMAIL = "dataverse@rudi.com";

	public CitationMetadataBlockMapper(FieldGenerator fieldGenerator) {
		super(fieldGenerator);
	}

	@Override
	public void datasetMetadataBlockElementToData(DatasetMetadataBlockElement datasetMetadataBlockElement,
			Metadata metadata) {
		// Nothing to do
	}

	@Override
	public String getDisplayName() {
		return CITATION_DISPLAY_NAME;
	}

	@Override
	public List<DatasetMetadataBlockElementField> createFields(Metadata metadata) {
		List<DatasetMetadataBlockElementField> fields = new ArrayList<>();

		addTitleField(metadata, fields);
		addAuthors(metadata, fields);
		addContacts(metadata, fields);
		addDescription(metadata, fields);
		addSubjectsField(fields);
		return fields;
	}

	private void addTitleField(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		DatasetMetadataBlockElementField titleField = createField(TITLE, metadata.getResourceTitle());
		fields.add(titleField);
	}

	private void addSubjectsField(List<DatasetMetadataBlockElementField> fields) {
		DatasetMetadataBlockElementField subjectField = createField(SUBJECT, Collections.singletonList(DEFAULT_SUBJECT));
		fields.add(subjectField);
	}

	private void addAuthors(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		Organization producer = metadata.getProducer();
		List<Map<String, Object>> summaryValues = new ArrayList<>();

		// Création d'un unique auteur
		Map<String, Object> summaryValue = new HashMap<>();

		DatasetMetadataBlockElementField authorNameField = createField(AUTHOR_NAME, producer.getOrganizationName());
		summaryValue.put(AUTHOR_NAME.getName(), authorNameField);

		addOptionalPrimitiveField(producer.getOrganizationAddress(), summaryValue, AUTHOR_AFFILIATION);

		summaryValues.add(summaryValue);

		DatasetMetadataBlockElementField authorField = createField(AUTHOR, summaryValues);
		fields.add(authorField);
	}

	private void addContacts(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {
		List<Contact> contacts = metadata.getContacts();
		List<Map<String, Object>> contactValues = new ArrayList<>();

		if (!CollectionUtils.isEmpty(contacts)) {
			for (Contact contact : contacts) {
				Map<String, Object> contactValue = new HashMap<>();

				addOptionalPrimitiveField(contact.getContactName(), contactValue, CONTACT_NAME);
				addOptionalPrimitiveField(contact.getOrganizationName(), contactValue, CONTACT_AFFILIATION);
				addOptionalPrimitiveField(contact.getEmail(), contactValue, CONTACT_EMAIL);

				contactValues.add(contactValue);
			}
		} else {
			// Création d'un unique contact
			Map<String, Object> contactValue = new HashMap<>();

			DatasetMetadataBlockElementField contactNameField = createField(CONTACT_EMAIL, DEFAULT_CONTACT_EMAIL);
			contactValue.put(CONTACT_EMAIL.getName(), contactNameField);

			contactValues.add(contactValue);
		}

		DatasetMetadataBlockElementField contactField = createField(CONTACT, contactValues);
		fields.add(contactField);
	}

	private void addDescription(Metadata metadata, List<DatasetMetadataBlockElementField> fields) {

		String rudiSynopsis = getSynopsisFrenchText(metadata);
		String descriptionText = !StringUtils.isEmpty(rudiSynopsis) ? rudiSynopsis : metadata.getResourceTitle();

		List<Map<String, Object>> descriptionValues = new ArrayList<>();

		// Création d'un unique auteur
		Map<String, Object> descriptionValue = new HashMap<>();

		DatasetMetadataBlockElementField descriptionValueField = createField(DESCRIPTION_VALUE, descriptionText);
		descriptionValue.put(DESCRIPTION_VALUE.getName(), descriptionValueField);

		DatasetMetadataBlockElementField descriptionDateField = createField(DESCRIPTION_DATE, LocalDate.now().toString());
		descriptionValue.put(DESCRIPTION_DATE.getName(), descriptionDateField);

		descriptionValues.add(descriptionValue);

		DatasetMetadataBlockElementField descriptionField = createField(DESCRIPTION, descriptionValues);
		fields.add(descriptionField);
	}

	private String getSynopsisFrenchText(Metadata metadata) {
		String text = null;

		for (DictionaryEntry dictionaryEntry : metadata.getSynopsis()) {
			if (dictionaryEntry.getLang().equals(Language.FR_FR)) {
				return dictionaryEntry.getText();
			}
		}
		return text;
	}
}
