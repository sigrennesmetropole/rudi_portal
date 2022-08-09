package org.rudi.microservice.projekt.storage.dao.targetaudience;

import org.rudi.microservice.projekt.storage.entity.TargetAudienceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface TargetAudienceDao extends JpaRepository<TargetAudienceEntity, Long> {
	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	TargetAudienceEntity findByUUID(UUID uuid);
}
