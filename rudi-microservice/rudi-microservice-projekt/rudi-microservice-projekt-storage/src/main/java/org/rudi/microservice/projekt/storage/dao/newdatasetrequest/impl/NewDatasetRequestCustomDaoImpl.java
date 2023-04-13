package org.rudi.microservice.projekt.storage.dao.newdatasetrequest.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequestSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.newdatasetrequest.NewDatasetRequestCustomDao;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class NewDatasetRequestCustomDaoImpl extends AbstractCustomDaoImpl<NewDatasetRequestEntity, NewDatasetRequestSearchCriteria> implements NewDatasetRequestCustomDao {

	public NewDatasetRequestCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, NewDatasetRequestEntity.class);
	}

	@Override
	public Page<NewDatasetRequestEntity> searchNewDatasetRequest(NewDatasetRequestSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(NewDatasetRequestSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<NewDatasetRequestEntity> root, List<Predicate> predicates) {
		if (StringUtils.isNotEmpty(searchCriteria.getTitleDescription())) {
			predicates.add(builder.or(buildPredicateStringCriteria(searchCriteria.getTitleDescription(), "title", builder, root),
					buildPredicateStringCriteria(searchCriteria.getTitleDescription(), "description", builder, root)));
		}
	}
}