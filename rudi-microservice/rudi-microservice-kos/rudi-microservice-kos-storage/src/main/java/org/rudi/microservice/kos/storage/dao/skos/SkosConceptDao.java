package org.rudi.microservice.kos.storage.dao.skos;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.kos.storage.entity.skos.SkosConceptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkosConceptDao extends JpaRepository<SkosConceptEntity, Long> {

	List<SkosConceptEntity> findAllByCode(String code);

	SkosConceptEntity findByCodeAndOfSchemeUuid(String code, UUID skosSchemeUuid);

	SkosConceptEntity findByUuidAndOfSchemeUuid(UUID skosConceptUuid, UUID skosSchemeUuid);

	List<SkosConceptEntity> findAllByOfSchemeUuid(UUID skosSchemeUuid);
}
