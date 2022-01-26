package org.rudi.microservice.kos.storage.dao.skos;

import java.util.UUID;

import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les Roles
 * 
 * @author FNI18300
 *
 */
@Repository
public interface SkosSchemeDao extends JpaRepository<SkosSchemeEntity, Long> {

	SkosSchemeEntity findByUUID(UUID uuid);

	// créer skos cocncept DAO
}
