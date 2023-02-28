package org.rudi.microservice.kalim.service.admin;

import java.util.List;
import java.util.UUID;

import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;

public interface AdminService {
	void repairResources() throws DataverseAPIException;

	void createMissingApis(List<UUID> globalIds);

	void deleteAllApis();
}
