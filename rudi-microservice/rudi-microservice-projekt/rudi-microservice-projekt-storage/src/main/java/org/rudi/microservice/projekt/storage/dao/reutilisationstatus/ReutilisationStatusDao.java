package org.rudi.microservice.projekt.storage.dao.reutilisationstatus;

import java.util.UUID;

import org.rudi.microservice.projekt.storage.entity.ReutilisationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReutilisationStatusDao extends JpaRepository<ReutilisationStatusEntity, Long> {
	ReutilisationStatusEntity findByUUID(UUID uuid);

	ReutilisationStatusEntity findByCode(String code);
}
