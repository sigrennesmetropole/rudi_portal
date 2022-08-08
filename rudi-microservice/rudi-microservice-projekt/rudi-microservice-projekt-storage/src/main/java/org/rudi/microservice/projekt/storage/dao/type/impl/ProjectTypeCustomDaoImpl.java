package org.rudi.microservice.projekt.storage.dao.type.impl;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.projekt.core.bean.ProjectTypeSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.type.ProjectTypeCustomDao;
import org.rudi.microservice.projekt.storage.entity.ProjectTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
public class ProjectTypeCustomDaoImpl extends AbstractCustomDaoImpl<ProjectTypeEntity, ProjectTypeSearchCriteria> implements ProjectTypeCustomDao {

	public ProjectTypeCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ProjectTypeEntity.class);
	}

	@Override
	public Page<ProjectTypeEntity> searchProjectTypes(ProjectTypeSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}
}
