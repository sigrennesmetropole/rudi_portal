package org.rudi.microservice.template.storage.dao.domaina.impl;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.template.core.bean.TemplateSearchCriteria;
import org.rudi.microservice.template.storage.dao.domaina.TemplateCustomDao;
import org.rudi.microservice.template.storage.entity.domaina.TemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class TemplateCustomDaoImpl extends AbstractCustomDaoImpl<TemplateEntity, TemplateSearchCriteria> implements TemplateCustomDao {

	public TemplateCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, TemplateEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<TemplateEntity> searchTemplates(TemplateSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(TemplateSearchCriteria searchCriteria, CriteriaBuilder builder, Root<TemplateEntity> root, List<Predicate> predicates) {
		// TODO ajouter les critères du TemplateSearchCriteria via les méthodes predicate...Criteria(...)
	}

}
