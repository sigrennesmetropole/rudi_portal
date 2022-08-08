package org.rudi.microservice.strukture.storage.dao.address;

import org.rudi.microservice.strukture.storage.entity.address.TelephoneAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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
