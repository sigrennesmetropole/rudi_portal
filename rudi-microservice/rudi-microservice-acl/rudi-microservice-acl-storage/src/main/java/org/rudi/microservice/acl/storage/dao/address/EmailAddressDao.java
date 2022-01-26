package org.rudi.microservice.acl.storage.dao.address;

import java.util.UUID;

import org.rudi.microservice.acl.storage.entity.address.EmailAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les EmailAddress
 *
 * @author NTR18299
 */
@Repository
public interface EmailAddressDao extends JpaRepository<EmailAddressEntity, Long> {
	EmailAddressEntity findByUUID(UUID uuid);
}
