package org.rudi.microservice.template.storage.dao.domaina;

import org.rudi.common.storage.dao.StampedRepository;
import org.rudi.microservice.template.storage.entity.domaina.ChildEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Dao pour ChildEntity héritant de AbstractStampedEntity
 */
@Repository
public interface ChildDao extends StampedRepository<ChildEntity> {

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 * @see org.rudi.common.storage.dao.StampedRepository#findByUUID(UUID)
	 */
	@SuppressWarnings("SpringDataMethodInconsistencyInspection") // on n'utilise pas findByUuid mais findByUUID
	@Nonnull
	ChildEntity findByUUID(UUID uuid);
}
