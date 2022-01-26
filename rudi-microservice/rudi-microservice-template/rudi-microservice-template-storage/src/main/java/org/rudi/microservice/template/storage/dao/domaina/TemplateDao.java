package org.rudi.microservice.template.storage.dao.domaina;

import java.util.UUID;

import org.rudi.microservice.template.storage.entity.domaina.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les Roles
 * 
 * @author FNI18300
 *
 */
@Repository
public interface TemplateDao extends JpaRepository<TemplateEntity, Long> {

	TemplateEntity findByUUID(UUID uuid);
}
