package org.rudi.microservice.konsent.storage.dao.treatmentversion;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.microservice.konsent.storage.entity.treatmentversion.TypologyTreatmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypologyTreatmentDao extends JpaRepository<TypologyTreatmentEntity, Long> {
	TypologyTreatmentEntity findByUuid(UUID uuid);
}
