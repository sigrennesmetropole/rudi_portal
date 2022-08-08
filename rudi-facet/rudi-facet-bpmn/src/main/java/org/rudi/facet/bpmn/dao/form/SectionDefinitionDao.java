/**
 * 
 */
package org.rudi.facet.bpmn.dao.form;

import java.util.UUID;

import org.rudi.facet.bpmn.entity.form.SectionDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author FNI18300
 *
 */
@Repository
public interface SectionDefinitionDao extends JpaRepository<SectionDefinitionEntity, Long> {

	SectionDefinitionEntity findByUuid(UUID uuid);

}
