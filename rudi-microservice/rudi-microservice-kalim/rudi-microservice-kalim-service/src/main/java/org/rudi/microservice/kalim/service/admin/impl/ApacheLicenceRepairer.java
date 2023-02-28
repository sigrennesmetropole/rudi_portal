package org.rudi.microservice.kalim.service.admin.impl;

import javax.annotation.Nonnull;

import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.springframework.stereotype.Component;

/**
 * Remplace toutes les licences "licence_Apache-2.0" par "apache-2.0"
 */
@Component
class ApacheLicenceRepairer extends LicenceRepairer {

	@Override
	@Nonnull
	protected String getInvalidLicenceLabel() {
		return "licence_Apache-2.0";
	}

	@Override
	@Nonnull
	protected LicenceStandard.LicenceLabelEnum getValidLicenceLabel() {
		return LicenceStandard.LicenceLabelEnum.APACHE_2_0;
	}
}
