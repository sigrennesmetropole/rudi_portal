package org.rudi.microservice.konsent.storage.dao.treatmentversion;

import java.util.UUID;

import org.rudi.microservice.konsent.storage.entity.treatmentversion.RetentionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetentionDao extends JpaRepository<RetentionEntity, Long> {
	RetentionEntity findByUuid(UUID uuid);
}
