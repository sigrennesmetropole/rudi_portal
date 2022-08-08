package org.rudi.facet.bpmn.dao.form;

import org.rudi.facet.bpmn.bean.form.ProcessFormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.entity.form.ProcessFormDefinitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 *
 */
public interface ProcessFormDefinitionCustomDao {

	/**
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	Page<ProcessFormDefinitionEntity> searchProcessFormDefintions(ProcessFormDefinitionSearchCriteria searchCriteria,
			Pageable pageable);

}
