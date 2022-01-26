package org.rudi.microservice.kos.storage.dao.skos;

import org.rudi.microservice.kos.storage.entity.skos.SkosConceptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SkosConceptDao extends JpaRepository<SkosConceptEntity, Long> {

    SkosConceptEntity findByCodeAndOfScheme_Uuid(String code, UUID skosSchemeUuid);

    SkosConceptEntity findByUuidAndOfScheme_Uuid(UUID skosConceptUuid, UUID skosSchemeUuid);

    List<SkosConceptEntity> findAllByOfScheme_Uuid(UUID skosSchemeUuid);
}
