package org.rudi.microservice.kalim.storage.dao.integration;

import java.util.UUID;

import org.rudi.microservice.kalim.core.bean.IntegrationRequestSearchCriteria;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IntegrationRequestCustomDao {

	Page<IntegrationRequestEntity> searchIntegrationRequests(IntegrationRequestSearchCriteria searchCriteria,
			Pageable pageable);

	IntegrationRequestEntity findByUUIDAndLock(UUID uuid);
}
