package org.rudi.microservice.strukture.storage.dao.address;

import org.rudi.microservice.strukture.storage.entity.address.WebsiteAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Dao pour WebSiteAddress
 *
 * @author NTR18299
 */

@Repository
public interface WebsiteAddressDao extends JpaRepository<WebsiteAddressEntity, Long> {
	WebsiteAddressEntity findByUUID(UUID uuid);
}
