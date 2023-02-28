package org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest;

import java.util.UUID;

import javax.annotation.Nullable;

import org.rudi.facet.bpmn.dao.workflow.AssetDescriptionDao;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.stereotype.Repository;

/**
 * Dao pour les Selfdatas héritant de AbstractLongIdEntity mais pas de AbstractStampedEntity
 *
 * @author FNI18300
 */
@Repository
public interface SelfdataInformationRequestDao extends AssetDescriptionDao<SelfdataInformationRequestEntity> {

	/**
	 * @see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods">doc
	 * Spring</a>
	 */
	@Nullable
	SelfdataInformationRequestEntity findByUuid(UUID uuid);
}
