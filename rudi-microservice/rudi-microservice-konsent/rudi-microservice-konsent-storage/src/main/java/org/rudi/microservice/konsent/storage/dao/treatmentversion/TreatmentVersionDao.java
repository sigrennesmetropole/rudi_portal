package org.rudi.microservice.konsent.storage.dao.treatmentversion;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TreatmentVersionDao extends JpaRepository<TreatmentVersionEntity, Long> {
	@Nonnull
	TreatmentVersionEntity findByUuid(UUID uuid);
}
