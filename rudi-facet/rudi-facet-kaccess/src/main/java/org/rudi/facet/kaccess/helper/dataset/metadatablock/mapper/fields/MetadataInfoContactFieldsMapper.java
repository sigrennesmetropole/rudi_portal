package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Contact;
import org.rudi.facet.kaccess.bean.Metadata;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.METADATA_INFO_CONTACT;

@Component
class MetadataInfoContactFieldsMapper extends MultipleValuedFieldsMapper<Contact> {

	MetadataInfoContactFieldsMapper(FieldGenerator fieldGenerator, MetadataInfoContactPrimitiveFieldsMapper primitiveFieldsMapper) {
		super(fieldGenerator, METADATA_INFO_CONTACT, primitiveFieldsMapper);
	}

	@Override
	@Nullable List<Contact> getMetadataElements(Metadata metadata) {
		return metadata.getMetadataInfo().getMetadataContacts();
	}

	@Override
	void setMetadataElements(@Nonnull Metadata metadata, @Nonnull List<Contact> childrenMetadata) {
		metadata.getMetadataInfo().setMetadataContacts(childrenMetadata);
	}

	@Override
	public int getRank() {
		return MetadataInfoFieldsMapper.RANK + 1;
	}
}
