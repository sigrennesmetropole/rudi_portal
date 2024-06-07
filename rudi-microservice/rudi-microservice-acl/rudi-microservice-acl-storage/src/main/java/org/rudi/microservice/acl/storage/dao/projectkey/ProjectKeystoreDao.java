package org.rudi.microservice.acl.storage.dao.projectkey;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeystoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les {@link ProjectKeystoreEntity}
 * 
 * @author FNI18300
 *
 */
@Repository
public interface ProjectKeystoreDao extends JpaRepository<ProjectKeystoreEntity, Long> {

	@Nullable
	ProjectKeystoreEntity findByUuid(UUID uuid);

	@Nullable
	ProjectKeystoreEntity findByProjectUuid(UUID uuid);
}
