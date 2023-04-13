package org.rudi.microservice.konsent.storage.dao.consent;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.microservice.konsent.storage.entity.consent.ConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les Konsents héritant de AbstractLongIdEntity mais pas de AbstractStampedEntity
 *
 * @author FNI18300
 */
@Repository
public interface ConsentDao extends JpaRepository<ConsentEntity, Long> {

	/**
	 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods">doc Spring</a>
	 */
	@Nullable
	ConsentEntity findByUuid(UUID uuid);

}
