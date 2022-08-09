package org.rudi.microservice.projekt.storage.dao.linkeddataset.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.common.storage.dao.PredicateListBuilder;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetCustomDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.val;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity.DATASET_UUID_FIELD;
import static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity.END_DATE_FIELD;
import static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity.PROJECT_FIELD;
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
	protected void addPredicates(LinkedDatasetSearchCriteria searchCriteria, CriteriaBuilder builder, Root<LinkedDatasetEntity> root, List<Predicate> predicates) {
		predicateStringCriteria(searchCriteria.getDatasetUuid(), DATASET_UUID_FIELD, predicates, builder, root);

		if (isTrue(searchCriteria.getCheckEndDate())) {
			final var endDateIsNull = root.get(END_DATE_FIELD).isNull();
			final var endDateIsNotOver = builder.greaterThan(root.get(END_DATE_FIELD), LocalDateTime.now());
			predicates.add(builder.or(endDateIsNull, endDateIsNotOver));
		}
	}

	@Override
	protected void addPredicates(PredicateListBuilder<LinkedDatasetEntity, LinkedDatasetSearchCriteria> builder) {
		val searchCriteria = builder.getSearchCriteria();
		builder
				.add(searchCriteria.getProjectOwnerUuid(), LinkedDatasetCustomDaoImpl::linkedDatasetIsInProjectOwnedBy)
				.add(searchCriteria.getStatus(), LinkedDatasetCustomDaoImpl::getEntityStatus, LinkedDatasetCustomDaoImpl::linkedDatasetHasStatus)
		;
	}

	private static Predicate linkedDatasetIsInProjectOwnedBy(Root<LinkedDatasetEntity> linkedDataset, UUID ownerUuid) {
		return linkedDataset.join(PROJECT_FIELD).get(ProjectEntity.FIELD_OWNER_UUID).in(ownerUuid);
	}

	private static Predicate linkedDatasetHasStatus(Root<LinkedDatasetEntity> linkedDataset, org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus status) {
		return linkedDataset.get(STATUS_FIELD).in(status);
	}

	private static org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus getEntityStatus(String name) {
		return org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetStatus.valueOf(name);
	}
}
