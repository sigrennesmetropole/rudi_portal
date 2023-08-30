package org.rudi.microservice.konsent.storage.dao.treatmentversion;

import java.util.UUID;

import org.rudi.microservice.konsent.storage.entity.treatmentversion.PurposeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurposeDao extends JpaRepository<PurposeEntity, Long> {
	PurposeEntity findByUuid(UUID uuid);
}
