package org.rudi.microservice.template.storage.dao.domaina;

import org.rudi.microservice.template.storage.entity.domaina.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Dao pour les Templates héritant de AbstractLongIdEntity mais pas de AbstractStampedEntity
 *
 * @author FNI18300
 */
@Repository
public interface TemplateDao extends JpaRepository<TemplateEntity, Long> {

	/**
	 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods">doc Spring</a>
	 */
	@Nullable
	TemplateEntity findByUuid(UUID uuid);

}
