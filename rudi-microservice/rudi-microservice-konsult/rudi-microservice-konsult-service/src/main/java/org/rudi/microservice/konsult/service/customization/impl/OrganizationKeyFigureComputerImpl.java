package org.rudi.microservice.konsult.service.customization.impl;

import org.apache.logging.log4j.util.Strings;
import org.rudi.facet.organization.bean.PagedOrganizationList;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.konsult.core.customization.KeyFigureData;
import org.rudi.microservice.konsult.core.customization.KeyFigureTypeData;
import org.rudi.microservice.konsult.service.customization.KeyFigureComputer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrganizationKeyFigureComputerImpl implements KeyFigureComputer {

	private final OrganizationHelper organizationHelper;

	@Override
	public KeyFigureTypeData getAcceptedData() {
		return KeyFigureTypeData.ORGANIZATION;
	}

	@Override
	public void compute(KeyFigureData keyFigureData) {
		// comptage des organizations
		try {
			PagedOrganizationList organizationResult = organizationHelper.searchOrganizations(0, 0,
					Strings.EMPTY);
			Long organizationCount = organizationResult.getTotal();
			keyFigureData.setCount(organizationCount);
		} catch (Exception e) {
			log.error("Erreur lors de la récupération du nombre d'organisations de la plateforme", e);
			keyFigureData.setCount(getDefaultKeyFigureValue());
		}
	}
}
