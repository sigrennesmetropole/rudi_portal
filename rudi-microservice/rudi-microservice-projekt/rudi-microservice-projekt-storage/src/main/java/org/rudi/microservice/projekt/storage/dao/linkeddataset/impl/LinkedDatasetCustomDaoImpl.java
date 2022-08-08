package org.rudi.microservice.projekt.storage.dao.linkeddataset.impl;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetCustomDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class LinkedDatasetCustomDaoImpl extends AbstractCustomDaoImpl<LinkedDatasetEntity, LinkedDatasetSearchCriteria> implements LinkedDatasetCustomDao {

	public LinkedDatasetCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, LinkedDatasetEntity.class);
	}

	@Override
	public Page<LinkedDatasetEntity> searchLinkedDatasets(LinkedDatasetSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(LinkedDatasetSearchCriteria searchCriteria, CriteriaBuilder builder, Root<LinkedDatasetEntity> root, List<Predicate> predicates) {
		return;
		/*if(StringUtils.isNotEmpty(searchCriteria.getTitleDescription())) {
			predicates.add(builder.or(buildPredicateStringCriteria(searchCriteria.getTitleDescription(), "title", builder, root),
					buildPredicateStringCriteria(searchCriteria.getTitleDescription(), "description", builder, root)));
		}*/
	}
}