package org.rudi.microservice.kalim.storage.dao.integration;

import java.util.UUID;

import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les integration request des metadata
 */
@Repository
public interface IntegrationRequestDao extends JpaRepository<IntegrationRequestEntity, Long> {

	IntegrationRequestEntity findByUuid(UUID uuid);

}
