package org.rudi.microservice.projekt.service.type;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Confidentiality;
import org.rudi.microservice.projekt.core.bean.ProjectType;
import org.rudi.microservice.projekt.core.bean.ProjectTypeSearchCriteria;
import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectTypeService {

	/**
	 * Search for project types
	 *
	 * @return paged projectType list
	 */
	Page<ProjectType> searchProjectTypes(ProjectTypeSearchCriteria searchCriteria, Pageable pageable);

	/**
	 * @throws org.springframework.dao.EmptyResultDataAccessException if entity was not found
	 */
	ProjectType getProjectType(UUID uuid);

	/**
	 * @param code du type
	 * Get project type by code. Return null if not exist
	 */
	ProjectType findByCode(String code);

	/**
	 * Create a project type
	 */
	ProjectType createProjectType(ProjectType projectType) throws AppServiceException;

	/**
	 * Update a project type entity
	 */
	ProjectType updateProjectType(ProjectType projectType) throws AppServiceException;

	/**
	 * Delete a project type entity
	 */
	void deleteProjectType(UUID uuid);

	ProjectType getProjectTypeByCode(String code);
}
