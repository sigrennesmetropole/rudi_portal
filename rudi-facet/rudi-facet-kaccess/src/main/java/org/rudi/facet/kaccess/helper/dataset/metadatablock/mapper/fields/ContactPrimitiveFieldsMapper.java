package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.kaccess.bean.Contact;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_EMAIL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_ROLE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT_SUMMARY;

@Component
class ContactPrimitiveFieldsMapper extends PrimitiveFieldsMapper<Contact> {

	ContactPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper, DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	@Override
	public void metadataToFields(Contact contact, Map<String, Object> fields) throws DataverseMappingException {
		createContactFields(contact, fields,
				CONTACT_ID,
				CONTACT_NAME,
				CONTACT_EMAIL,
				CONTACT_ORGANIZATION_NAME,
				CONTACT_ROLE,
				CONTACT_SUMMARY);
	}

	@Nonnull
	@Override
	public Contact fieldsToMetadata(@Nonnull MapOfFields fields) {
		return buildContact(fields, CONTACT_ID, CONTACT_NAME, CONTACT_EMAIL, CONTACT_ORGANIZATION_NAME, CONTACT_ROLE, CONTACT_SUMMARY)
				.contactSummary(fields.get(CONTACT_SUMMARY).getValueAsString());
	}

	@Nullable
	@Override
	public Contact defaultMetadata() {
		return null;
	}

}
