package org.rudi.microservice.strukture.service.organization.impl.fields;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UrlProcessor implements CreateOrganizationFieldProcessor, UpdateOrganizationFieldProcessor {
	@Override
	public void processBeforeCreate(OrganizationEntity organization) throws AppServiceBadRequestException {
		if (organization != null && StringUtils.isNotBlank(organization.getUrl()) &&
				organization.getUrl().length() > 80) {
			throw new AppServiceBadRequestException("L'url de l'organisation est trop longue ( > 80 caractères)");
		}
	}

	@Override
	public void processBeforeUpdate(Organization organization, OrganizationEntity existingOrganization) throws AppServiceBadRequestException {
		if (organization != null && StringUtils.isNotBlank(organization.getUrl()) &&
				organization.getUrl().length() > 80) {
			throw new AppServiceBadRequestException("La nouvelle url de l'organisation est trop longue ( > 80 caractères)");
		}
	}
}
