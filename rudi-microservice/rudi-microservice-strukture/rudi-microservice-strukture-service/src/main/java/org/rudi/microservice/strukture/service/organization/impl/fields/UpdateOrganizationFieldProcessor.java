package org.rudi.microservice.strukture.service.organization.impl.fields;

import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;

public interface UpdateOrganizationFieldProcessor {

	void processBeforeUpdate(Organization organization, OrganizationEntity existingOrganization);

}
