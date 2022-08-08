package org.rudi.microservice.projekt.storage.dao.confidentiality;

import org.rudi.microservice.projekt.storage.entity.ConfidentialityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.UUID;

@Repository
public interface ConfidentialityDao extends JpaRepository<ConfidentialityEntity, Long> {

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	ConfidentialityEntity findByUUID(UUID uuid);

	ConfidentialityEntity findByCode(String code);
}
