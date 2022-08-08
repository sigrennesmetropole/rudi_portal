package org.rudi.facet.bpmn.dao.form;

import org.rudi.facet.bpmn.bean.form.SectionDefinitionSearchCriteria;
import org.rudi.facet.bpmn.entity.form.SectionDefinitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 *
 */
public interface SectionDefinitionCustomDao {

	/**
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	Page<SectionDefinitionEntity> searchSectionDefinitions(SectionDefinitionSearchCriteria searchCriteria,
			Pageable pageable);

}
