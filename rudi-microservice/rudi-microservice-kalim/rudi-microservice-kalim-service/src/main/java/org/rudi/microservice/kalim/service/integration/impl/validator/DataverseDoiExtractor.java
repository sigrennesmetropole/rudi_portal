package org.rudi.microservice.kalim.service.integration.impl.validator;

import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.springframework.stereotype.Component;

@Component
class DataverseDoiExtractor implements FieldExtractor<String> {

	@Override
	public FieldSpec getField() {
		return RudiMetadataField.DATAVERSE_DOI;
	}

	@Override
	public String getFieldValue(Metadata metadata) {
		return metadata.getDataverseDoi();
	}
}
