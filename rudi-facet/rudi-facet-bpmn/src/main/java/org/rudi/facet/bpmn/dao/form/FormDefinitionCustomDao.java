package org.rudi.facet.bpmn.dao.form;

import org.rudi.facet.bpmn.bean.form.FormDefinitionSearchCriteria;
import org.rudi.facet.bpmn.entity.form.FormDefinitionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author FNI18300
 *
 */
public interface FormDefinitionCustomDao {

	/**
	 * 
	 * @param searchCriteria
	 * @param sortCriteria
	 * @return
	 */
	Page<FormDefinitionEntity> searchFormDefinitions(FormDefinitionSearchCriteria searchCriteria, Pageable pageable);

}
