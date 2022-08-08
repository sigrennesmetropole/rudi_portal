package org.rudi.microservice.strukture.storage.dao.address;

import org.rudi.microservice.strukture.storage.entity.address.AbstractAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Dao pour les AbstractAddressEntity
 *
 * @author NTR18299
 */
@Repository
public interface AbstractAddressDao extends JpaRepository<AbstractAddressEntity, Long> {
	AbstractAddressEntity findByUUID(UUID uuid);
}
