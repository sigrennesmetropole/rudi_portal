package org.rudi.microservice.projekt.storage.dao.type;

import org.rudi.microservice.projekt.core.bean.ProjectTypeSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectTypeCustomDao {
	Page<ProjectTypeEntity> searchProjectTypes(ProjectTypeSearchCriteria searchCriteria, Pageable pageable);
}
