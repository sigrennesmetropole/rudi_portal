package org.rudi.microservice.apigateway.storage.dao.throttling;

import java.util.UUID;

import org.rudi.common.storage.dao.StampedRepository;
import org.rudi.microservice.apigateway.storage.entity.throttling.ThrottlingEntity;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les Apigateways héritant de AbstractLongIdEntity mais pas de AbstractStampedEntity
 *
 * @author FNI18300
 */
@Repository
public interface ThrottlingDao extends StampedRepository<ThrottlingEntity> {

	ThrottlingEntity findNullableByUuid(UUID uuid);

}
