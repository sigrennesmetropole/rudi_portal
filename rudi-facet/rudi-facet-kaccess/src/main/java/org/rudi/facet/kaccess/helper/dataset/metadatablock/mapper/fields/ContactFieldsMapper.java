package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Contact;
import org.rudi.facet.kaccess.bean.Metadata;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.CONTACT;

@Component
class ContactFieldsMapper extends MultipleValuedFieldsMapper<Contact> {

	ContactFieldsMapper(FieldGenerator fieldGenerator, ContactPrimitiveFieldsMapper primitiveFieldsMapper) {
		super(fieldGenerator, CONTACT, primitiveFieldsMapper);
	}

	@Override
	@Nullable List<Contact> getMetadataElements(Metadata metadata) {
		return metadata.getContacts();
	}

	@Override
	void setMetadataElements(@Nonnull Metadata metadata, @Nonnull List<Contact> childrenMetadata) {
		metadata.setContacts(childrenMetadata);
	}
}
