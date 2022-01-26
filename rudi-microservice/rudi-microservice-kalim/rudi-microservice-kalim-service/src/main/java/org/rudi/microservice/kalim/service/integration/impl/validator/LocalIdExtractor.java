package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.springframework.stereotype.Component;

@Component
class LocalIdExtractor implements FieldExtractor<String> {

	@Override
	public FieldSpec getField() {
		return RudiMetadataField.LOCAL_ID;
	}

	@Override
	public String getFieldValue(Metadata metadata) {
		return metadata.getLocalId();
	}
}
