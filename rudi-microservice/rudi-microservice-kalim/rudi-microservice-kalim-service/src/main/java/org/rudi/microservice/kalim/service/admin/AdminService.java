package org.rudi.microservice.kalim.service.admin;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.microservice.kalim.core.bean.OrganizationsReparationReport;

public interface AdminService {
	void repairResources() throws DataverseAPIException;

	OrganizationsReparationReport repairOrganizations() throws AppServiceException;

	void createMissingApis(List<UUID> globalIds);

	void deleteAllApis();
}
