package org.rudi.microservice.apigateway.storage.dao.api.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.storage.dao.api.ApiCustomDao;
import org.rudi.microservice.apigateway.storage.entity.api.ApiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ApiCustomDaoImpl extends AbstractCustomDaoImpl<ApiEntity, ApiSearchCriteria> implements ApiCustomDao {

	public ApiCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ApiEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<ApiEntity> searchApis(ApiSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(ApiSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<?> criteriaQuery, Root<ApiEntity> root, List<Predicate> predicates) {
		predicateStringCriteria(searchCriteria.getGlobalId(), "globalId", predicates, builder, root);
		predicateStringCriteria(searchCriteria.getContract(), "contract", predicates, builder, root);
		predicateStringCriteria(searchCriteria.getMediaId(), "mediaId", predicates, builder, root);
		predicateStringCriteria(searchCriteria.getProviderId(), "providerId", predicates, builder, root);
		predicateStringCriteria(searchCriteria.getProducerId(), "producerId", predicates, builder, root);
	}

}
