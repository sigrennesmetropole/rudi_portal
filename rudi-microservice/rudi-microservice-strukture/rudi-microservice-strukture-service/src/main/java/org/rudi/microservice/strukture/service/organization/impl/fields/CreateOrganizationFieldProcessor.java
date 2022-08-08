package org.rudi.microservice.strukture.service.organization.impl.fields;

import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;

public interface CreateOrganizationFieldProcessor {

	void processBeforeCreate(OrganizationEntity organization);

}
