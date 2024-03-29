package org.rudi.microservice.acl.storage.dao.address;

import java.util.UUID;

import org.rudi.microservice.acl.storage.entity.address.AbstractAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les AbstractAddressEntity
 *
 * @author NTR18299
 */
@Repository
public interface AbstractAddressDao extends JpaRepository<AbstractAddressEntity, Long> {
	AbstractAddressEntity findByUUID(UUID uuid);
}
