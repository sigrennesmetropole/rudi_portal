package org.rudi.microservice.projekt.storage.dao.support;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.common.storage.dao.StampedRepository;
import org.rudi.microservice.projekt.storage.entity.SupportEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportDao extends StampedRepository<SupportEntity> {

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	SupportEntity findByUUID(UUID uuid);
}
