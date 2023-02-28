package org.rudi.facet.doks.dao;

import java.util.UUID;

import org.rudi.facet.doks.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface DocumentDao extends JpaRepository<DocumentEntity, Long> {
	DocumentEntity findByUuid(UUID uuid);
}
