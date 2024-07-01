package org.rudi.microservice.projekt.storage.dao.reutilisationstatus;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.microservice.projekt.storage.entity.ReutilisationStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReutilisationStatusDao extends JpaRepository<ReutilisationStatusEntity, Long> {

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	ReutilisationStatusEntity findByUUID(UUID uuid);

	ReutilisationStatusEntity findByCode(String code);
}
