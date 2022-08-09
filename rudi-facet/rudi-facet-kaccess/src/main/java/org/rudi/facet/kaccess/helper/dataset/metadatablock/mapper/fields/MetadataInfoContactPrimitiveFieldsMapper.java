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

import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_EMAIL;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_ID;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_ORGANIZATION_NAME;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_ROLE;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT_SUMMARY;

@Component
class MetadataInfoContactPrimitiveFieldsMapper extends PrimitiveFieldsMapper<Contact> {

	MetadataInfoContactPrimitiveFieldsMapper(FieldGenerator fieldGenerator, ObjectMapper objectMapper, DateTimeMapper dateTimeMapper) {
		super(fieldGenerator, objectMapper, dateTimeMapper);
	}

	public void metadataToFields(Contact contact, Map<String, Object> fields) throws DataverseMappingException {
		createContactFields(contact, fields,
				METADATA_INFO_CONTACT_ID,
				METADATA_INFO_CONTACT_NAME,
				METADATA_INFO_CONTACT_EMAIL,
				METADATA_INFO_CONTACT_ORGANIZATION_NAME,
				METADATA_INFO_CONTACT_ROLE,
				METADATA_INFO_CONTACT_SUMMARY);
	}

	@Nonnull
	@Override
	public Contact fieldsToMetadata(@Nonnull MapOfFields fields) throws DataverseMappingException {
		return buildContact(fields,
				METADATA_INFO_CONTACT_ID,
				METADATA_INFO_CONTACT_NAME,
				METADATA_INFO_CONTACT_EMAIL,
				METADATA_INFO_CONTACT_ORGANIZATION_NAME,
				METADATA_INFO_CONTACT_ROLE,
				METADATA_INFO_CONTACT_SUMMARY);
	}

	@Nullable
	@Override
	public Contact defaultMetadata() {
		return null;
	}

}
