/**
 * RUDI Portail
 */
package org.rudi.facet.bpmn.dao.workflow;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.facet.bpmn.entity.workflow.AssetDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author FNI18300
 *
 */
@NoRepositoryBean
public interface AssetDescriptionDao<E extends AssetDescriptionEntity> extends JpaRepository<E, Long> {

	@Nullable
	E findByUuid(UUID uuid);
}
