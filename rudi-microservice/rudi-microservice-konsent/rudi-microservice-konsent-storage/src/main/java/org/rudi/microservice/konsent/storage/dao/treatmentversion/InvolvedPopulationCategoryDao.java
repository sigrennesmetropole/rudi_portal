package org.rudi.microservice.konsent.storage.dao.treatmentversion;

import java.util.UUID;

import org.rudi.microservice.konsent.storage.entity.treatmentversion.InvolvedPopulationCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvolvedPopulationCategoryDao extends JpaRepository<InvolvedPopulationCategoryEntity, Long> {
	InvolvedPopulationCategoryEntity findByUuid(UUID uuid);
}
