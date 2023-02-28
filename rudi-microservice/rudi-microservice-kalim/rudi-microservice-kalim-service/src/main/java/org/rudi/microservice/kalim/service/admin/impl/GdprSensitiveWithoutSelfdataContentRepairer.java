package org.rudi.microservice.kalim.service.admin.impl;

import org.rudi.facet.dataverse.bean.DatasetVersion;
import org.rudi.facet.dataverse.helper.query.FilterQuery;
import org.rudi.facet.kaccess.constant.RudiMetadataField;
import org.springframework.stereotype.Component;

@Component
class GdprSensitiveWithoutSelfdataContentRepairer implements ResourceRepairer {
	@Override
	public String getQuery() {
		return new FilterQuery()
				.add(RudiMetadataField.GDPR_SENSITIVE, true)
				.add(RudiMetadataField.EXT_SELFDATA_CONTENT, null)
				.joinWithAnd();
	}

	@Override
	public void repairResource(DatasetVersion datasetVersion) {
		FieldUtils.setFieldValue(datasetVersion, RudiMetadataField.ACCESS_CONDITION, RudiMetadataField.GDPR_SENSITIVE, false);
	}
}
