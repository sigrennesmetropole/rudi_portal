package org.rudi.microservice.acl.storage.dao.address;

import java.util.UUID;

import org.rudi.microservice.acl.storage.entity.address.TelephoneAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les TelephoneAddress
 *
 * @author NTR18299
 *
 */

@Repository
public interface TelephoneAddressDao extends JpaRepository<TelephoneAddressEntity, Long> {

	TelephoneAddressEntity findByUUID(UUID uuid);
}
