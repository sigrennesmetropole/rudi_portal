package org.rudi.microservice.strukture.service.organization.impl.fields;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OpeningDateProcessor implements CreateOrganizationFieldProcessor, UpdateOrganizationFieldProcessor {

	@Override
	public void processBeforeCreate(OrganizationEntity organization) throws AppServiceBadRequestException {
		if (organization != null && organization.getOpeningDate() == null) {
			throw new AppServiceBadRequestException("La date d'ouverture de l'organisation est obligatoire");
		}
	}

	@Override
	public void processBeforeUpdate(Organization organization, OrganizationEntity existingOrganization) {
		// Rien à tester en MAJ la date peut ne pas bouger
	}
}
