package org.rudi.microservice.kalim.storage.dao.integration;

import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les integration request error
 */
@Repository
public interface IntegrationRequestErrorDao extends JpaRepository<IntegrationRequestErrorEntity, Long> {

}
