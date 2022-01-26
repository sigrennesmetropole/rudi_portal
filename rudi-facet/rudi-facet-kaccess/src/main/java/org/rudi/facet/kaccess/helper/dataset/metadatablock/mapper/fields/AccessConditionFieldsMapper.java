package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

import static org.rudi.facet.kaccess.constant.RudiMetadataField.ACCESS_CONDITION;

@Component
public class AccessConditionFieldsMapper extends FieldsMapper<MetadataAccessCondition> {

	public AccessConditionFieldsMapper(FieldGenerator fieldGenerator, AccessConditionPrimiviteFieldsMapper primiviteFieldsMapper, AccessConditionCompoundFieldsMapper compoundFieldsMapper) {
		super(fieldGenerator, ACCESS_CONDITION, primiviteFieldsMapper, compoundFieldsMapper);
	}

	@Override
	MetadataAccessCondition getMetadataElement(Metadata metadata) {
		return metadata.getAccessCondition();
	}

	@Override
	void setMetadataElement(@Nonnull Metadata metadata, @Nonnull MetadataAccessCondition childMetadata) {
		metadata.setAccessCondition(childMetadata);
	}
}
