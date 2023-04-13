package org.rudi.microservice.konsent.storage.dao.treatmentversion;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.konsent.storage.entity.treatmentversion.SecurityMeasureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityMeasureDao extends JpaRepository<SecurityMeasureEntity, Long> {
	SecurityMeasureEntity findByUuid(UUID uuid);
}
