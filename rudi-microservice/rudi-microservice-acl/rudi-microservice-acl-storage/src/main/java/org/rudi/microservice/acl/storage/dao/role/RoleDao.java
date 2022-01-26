package org.rudi.microservice.acl.storage.dao.role;

import java.util.UUID;

import org.rudi.microservice.acl.storage.entity.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les roles des utilisateurs
 * 
 * @author FNI18300
 *
 */
@Repository
public interface RoleDao extends JpaRepository<RoleEntity, Long> {

	RoleEntity findByUUID(UUID uuid);
}
