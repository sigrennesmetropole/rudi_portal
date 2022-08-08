package org.rudi.microservice.strukture.storage.dao.address;

import org.rudi.microservice.strukture.storage.entity.address.PostalAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Dao pour les PostalAddress
 *
 * @author NTR18299
 */

@Repository
public interface PostalAddressDao extends JpaRepository<PostalAddressEntity, Long> {
    PostalAddressEntity findByUUID(UUID uuid);

}
