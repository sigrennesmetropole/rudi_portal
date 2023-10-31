package org.rudi.microservice.strukture.storage.dao.provider;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.strukture.storage.entity.provider.ProviderEntity;
import org.springframework.dao.EmptyResultDataAccessException;
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

	@Nonnull
	ProviderEntity findByUUID(UUID uuid) throws EmptyResultDataAccessException;

	ProviderEntity findByCode(String code);
}
