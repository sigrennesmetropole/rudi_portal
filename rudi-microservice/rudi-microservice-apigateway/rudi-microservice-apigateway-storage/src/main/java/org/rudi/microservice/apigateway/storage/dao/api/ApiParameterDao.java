package org.rudi.microservice.apigateway.storage.dao.api;

import java.util.UUID;

import org.rudi.microservice.apigateway.storage.entity.api.ApiParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour ChildEntity héritant de AbstractStampedEntity
 */
@Repository
public interface ApiParameterDao extends JpaRepository<ApiParameterEntity, Long> {

	ApiParameterEntity findByUUID(UUID uuid);
}
