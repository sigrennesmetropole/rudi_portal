package org.rudi.microservice.apigateway.storage.dao.api;

import org.rudi.microservice.apigateway.storage.entity.api.ApiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Dao pour les Apigateways héritant de AbstractLongIdEntity mais pas de AbstractStampedEntity
 *
 * @author FNI18300
 */
@Repository
public interface ApiDao extends JpaRepository<ApiEntity, Long> {

	/**
	 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods">doc Spring</a>
	 */
	@Nullable
	ApiEntity findByUuid(UUID uuid);

}
