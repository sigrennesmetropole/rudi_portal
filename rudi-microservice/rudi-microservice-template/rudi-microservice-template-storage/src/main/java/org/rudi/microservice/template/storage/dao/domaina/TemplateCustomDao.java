package org.rudi.microservice.template.storage.dao.domaina;

import org.rudi.microservice.template.core.bean.TemplateSearchCriteria;
import org.rudi.microservice.template.storage.entity.domaina.TemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TemplateCustomDao {
	Page<TemplateEntity> searchTemplates(TemplateSearchCriteria searchCriteria, Pageable pageable);
}
