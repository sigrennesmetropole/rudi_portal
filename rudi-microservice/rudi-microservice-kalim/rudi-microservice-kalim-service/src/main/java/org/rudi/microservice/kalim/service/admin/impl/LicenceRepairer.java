package org.rudi.microservice.kalim.service.admin.impl;

import javax.annotation.Nonnull;

import org.rudi.facet.dataverse.bean.DatasetVersion;
import org.rudi.facet.dataverse.helper.query.FilterQuery;
import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.rudi.facet.kaccess.constant.RudiMetadataField;

abstract class LicenceRepairer implements ResourceRepairer {
	@Override
	public String getQuery() {
		final var licenceLabel = RudiMetadataField.LICENCE_LABEL;

		return new FilterQuery()
				.add(licenceLabel, getInvalidLicenceLabel())
				.joinWithOr();
	}

	@Nonnull
	protected abstract String getInvalidLicenceLabel();

	@Override
	public void repairResource(DatasetVersion datasetVersion) {
		FieldUtils.setFieldValue(datasetVersion, RudiMetadataField.ACCESS_CONDITION, RudiMetadataField.LICENCE_LABEL, getValidLicenceLabel().getValue());
	}

	@Nonnull
	protected abstract LicenceStandard.LicenceLabelEnum getValidLicenceLabel();
}
