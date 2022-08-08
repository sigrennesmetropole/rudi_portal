package org.rudi.microservice.projekt.storage.dao.territory;

import org.rudi.microservice.projekt.storage.entity.TerritorialScaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.UUID;

@Repository
public interface TerritorialScaleDao extends JpaRepository<TerritorialScaleEntity, Long> {

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	TerritorialScaleEntity findByUUID(UUID uuid);
}
