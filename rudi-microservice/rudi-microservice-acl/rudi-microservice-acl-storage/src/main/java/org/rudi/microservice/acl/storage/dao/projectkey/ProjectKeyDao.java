package org.rudi.microservice.acl.storage.dao.projectkey;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les {@link ProjectKeyEntity}
 * 
 * @author FNI18300
 *
 */
@Repository
public interface ProjectKeyDao extends JpaRepository<ProjectKeyEntity, Long> {

	@Nullable
	ProjectKeyEntity findByUuid(UUID uuid);

}
