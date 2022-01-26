/**
 * 
 */
package org.rudi.common.storage.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.rudi.common.storage.entity.AbstractStampedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author FNI18300
 *
 */
@NoRepositoryBean
public interface StampedRepository<T extends AbstractStampedEntity> extends JpaRepository<T, Long>, Serializable {

	List<T> findActive(Date d);

	T findByUUID(UUID uuid);

}
