package org.rudi.microservice.kalim.service.admin.impl;

import javax.annotation.Nonnull;

import org.rudi.facet.kaccess.bean.LicenceStandard;
import org.springframework.stereotype.Component;

/**
 * Remplace toutes les licences "open-source-licence" par "public-domain-cc0"
 */
@Component
class OpenSourceLicenceRepairer extends LicenceRepairer {

	@Nonnull
	@Override
	protected String getInvalidLicenceLabel() {
		return "open-source-licence";
	}

	@Nonnull
	@Override
	protected LicenceStandard.LicenceLabelEnum getValidLicenceLabel() {
		return LicenceStandard.LicenceLabelEnum.PUBLIC_DOMAIN_CC0;
	}
}
