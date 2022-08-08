package org.rudi.microservice.strukture.storage.dao.address;

import org.rudi.microservice.strukture.storage.entity.address.EmailAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Dao pour les EmailAddress
 *
 * @author NTR18299
 */
@Repository
public interface EmailAddressDao extends JpaRepository<EmailAddressEntity, Long> {
    EmailAddressEntity findByUUID(UUID uuid);
}
