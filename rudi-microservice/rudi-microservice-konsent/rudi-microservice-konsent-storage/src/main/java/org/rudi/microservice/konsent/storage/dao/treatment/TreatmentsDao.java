package org.rudi.microservice.konsent.storage.dao.treatment;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentsDao extends JpaRepository<TreatmentEntity, Long> {
	@Nonnull
	TreatmentEntity findByUuid(UUID uuid);
}