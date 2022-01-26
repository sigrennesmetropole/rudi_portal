package org.rudi.microservice.acl.storage.dao.address;

import java.util.UUID;

import org.rudi.microservice.acl.storage.entity.address.PostalAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les PostalAddress
 *
 * @author NTR18299
 */

@Repository
public interface PostalAddressDao extends JpaRepository<PostalAddressEntity, Long> {
	PostalAddressEntity findByUUID(UUID uuid);

}
