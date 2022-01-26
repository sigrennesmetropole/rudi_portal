package org.rudi.microservice.providers.storage.dao.provider;

import java.util.UUID;

import org.rudi.microservice.providers.storage.entity.provider.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les Roles
 * 
 * @author FNI18300
 *
 */
@Repository
public interface ProviderDao extends JpaRepository<ProviderEntity, Long> {

	ProviderEntity findByUUID(UUID uuid);
	ProviderEntity findByCode(String code);
}
