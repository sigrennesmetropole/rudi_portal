package org.rudi.microservice.projekt.storage.dao.targetaudience;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.common.storage.dao.StampedRepository;
import org.rudi.microservice.projekt.storage.entity.TargetAudienceEntity;

public interface TargetAudienceDao extends StampedRepository<TargetAudienceEntity> {
	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	TargetAudienceEntity findByUUID(UUID uuid);
}
