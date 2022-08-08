/**
 * 
 */
package org.rudi.common.storage.dao;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author FNI18300
 *
 */
@NoRepositoryBean
public interface StampedRepository<T extends AbstractStampedEntity> extends JpaRepository<T, Long>, Serializable {

	List<T> findActive(Date d);

	/**
	 * <b>Attention</b> : cette méthode ne peut être utilisée que pour des entités {@link AbstractStampedEntity}.
	 * Si on souhaite faire une recherche par UUID pour d'autres entités, il faut utiliser à la place dans la DAO :
	 *
	 * <pre>{@code
	 * T findByUuid(UUID uuid);
	 * }</pre>
	 *
	 * @throws org.springframework.dao.EmptyResultDataAccessException si l'entité demandée n'a pas été trouvée
	 */
	@Nonnull
	T findByUUID(UUID uuid);

}
