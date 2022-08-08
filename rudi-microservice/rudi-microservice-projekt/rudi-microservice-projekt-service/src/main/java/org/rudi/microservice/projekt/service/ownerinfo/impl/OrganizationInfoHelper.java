package org.rudi.microservice.projekt.service.ownerinfo.impl;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class OrganizationInfoHelper implements OwnerInfoHelper {
	private final OrganizationHelper organizationHelper;

	@Override
	public boolean isHelperFor(OwnerType ownerType) {
		return ownerType == OwnerType.ORGANIZATION;
	}

	@Override
	public OwnerInfo getOwnerInfo(UUID ownerUuid) throws GetOrganizationException, AppServiceNotFoundException {
		final var organization = organizationHelper.getOrganization(ownerUuid);
		if (organization == null) {
			throw new AppServiceNotFoundException(Organization.class, ownerUuid);
		}
		return new OwnerInfo().name(organization.getName());
	}
}
