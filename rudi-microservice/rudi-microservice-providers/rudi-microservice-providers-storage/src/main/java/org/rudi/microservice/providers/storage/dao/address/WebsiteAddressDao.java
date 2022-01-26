package org.rudi.microservice.providers.storage.dao.address;

import java.util.UUID;

import org.rudi.microservice.providers.storage.entity.address.WebsiteAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dao pour WebSiteAddress
 *
 * @author NTR18299
 */

@Repository
public interface WebsiteAddressDao extends JpaRepository<WebsiteAddressEntity, Long> {
	WebsiteAddressEntity findByUUID(UUID uuid);
}
