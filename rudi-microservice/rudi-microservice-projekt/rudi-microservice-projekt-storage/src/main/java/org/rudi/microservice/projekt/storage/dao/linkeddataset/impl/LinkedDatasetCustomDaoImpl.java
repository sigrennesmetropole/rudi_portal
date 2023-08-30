package org.rudi.microservice.projekt.storage.dao.linkeddataset.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections.CollectionUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetCustomDao;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity.DATASET_CONFIDENTIALITY_FIELD;
import static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity.DATASET_UUID_FIELD;
import static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity.END_DATE_FIELD;
import static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity.STATUS_FIELD;

@Repository
public class LinkedDatasetCustomDaoImpl extends AbstractCustomDaoImpl<LinkedDatasetEntity, LinkedDatasetSearchCriteria> implements LinkedDatasetCustomDao {

	public LinkedDatasetCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, LinkedDatasetEntity.class);
	}

	@Override
	public Page<LinkedDatasetEntity> searchLinkedDatasets(LinkedDatasetSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(LinkedDatasetSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<LinkedDatasetEntity> root, List<Predicate> predicates) {
		predicateStringCriteria(searchCriteria.getDatasetUuid(), DATASET_UUID_FIELD, predicates, builder, root);
   		// Les demandes non expirées
		if (isTrue(searchCriteria.getEndDateIsNotOver())) {
			final var endDateIsNotOver = builder.greaterThan(root.get(END_DATE_FIELD), LocalDateTime.now());
			predicates.add(endDateIsNotOver);
		}
		// Les demandes expirées
		if (isTrue(searchCriteria.getEndDateIsOver())) {
			final var endDateIsOver = builder.lessThan(root.get(END_DATE_FIELD), LocalDateTime.now());
			predicates.add(endDateIsOver);
		}
		// Les demandes n'ayant pas de endDate
		if (isTrue(searchCriteria.getEndDateIsNull())) {
			final var endDateIsNull = root.get(END_DATE_FIELD).isNull();
			predicates.add(endDateIsNull);
		}

		if (CollectionUtils.isNotEmpty(searchCriteria.getProjectOwnerUuids())) {
			Subquery<Long> subqueryProjectOwner = criteriaQuery.subquery(Long.class);
			Root<ProjectEntity> subRoot = subqueryProjectOwner.from(ProjectEntity.class);
			Join<ProjectEntity, LinkedDatasetEntity> joinDatasetRequest = subRoot.join(ProjectEntity.FIELD_LINKED_DATASET, JoinType.INNER);
			subqueryProjectOwner.select(joinDatasetRequest.get(LinkedDatasetEntity.FIELD_ID));
			subqueryProjectOwner.where(subRoot.get(ProjectEntity.FIELD_OWNER_UUID).in(searchCriteria.getProjectOwnerUuids()));
			predicates.add(root.get(ProjectEntity.FIELD_ID).in(subqueryProjectOwner));
		}

		if (CollectionUtils.isNotEmpty(searchCriteria.getStatus())) {
			List<LinkedDatasetStatus> statusList = searchCriteria.getStatus().stream().map(status ->
					LinkedDatasetStatus.valueOf(status.name())
			).collect(Collectors.toList());
			predicates.add(root.get(STATUS_FIELD).in(statusList));
		}

		if (searchCriteria.getDatasetUuid() != null) {
			predicates.add(root.get(LinkedDatasetEntity.DATASET_UUID_FIELD).in(searchCriteria.getDatasetUuid()));
		}

		if (searchCriteria.getDatasetConfidentiality() != null) {
			DatasetConfidentiality confidentiality = DatasetConfidentiality.valueOf(searchCriteria.getDatasetConfidentiality());
			predicates.add(root.get(DATASET_CONFIDENTIALITY_FIELD).in(confidentiality));
		}
	}
}
