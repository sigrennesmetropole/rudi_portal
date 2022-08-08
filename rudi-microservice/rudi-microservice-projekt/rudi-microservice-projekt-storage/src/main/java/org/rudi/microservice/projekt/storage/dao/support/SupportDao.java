package org.rudi.microservice.projekt.storage.dao.support;

import org.rudi.microservice.projekt.storage.entity.SupportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.UUID;

@Repository
public interface SupportDao extends JpaRepository<SupportEntity, Long> {

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	SupportEntity findByUUID(UUID uuid);
}
