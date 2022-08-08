package org.rudi.microservice.strukture.storage.dao.organization;

import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationDao extends JpaRepository<OrganizationEntity, Long> {

	OrganizationEntity findByUuid(UUID uuid);

}
