package org.rudi.microservice.projekt.storage.dao.newdatasetrequest.impl;

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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequestSearchCriteria;
import org.rudi.microservice.projekt.storage.dao.newdatasetrequest.NewDatasetRequestCustomDao;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestEntity;
import org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestStatus;
import org.rudi.microservice.projekt.storage.entity.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
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

		if (CollectionUtils.isNotEmpty(searchCriteria.getProjectOwnerUuids())) {
			Subquery<Long> subqueryProjectOwner = criteriaQuery.subquery(Long.class);
			Root<ProjectEntity> subRoot = subqueryProjectOwner.from(ProjectEntity.class);
			Join<ProjectEntity, NewDatasetRequestEntity> joinDatasetRequest = subRoot.join(ProjectEntity.FIELD_DATASET_REQUESTS, JoinType.INNER);
			subqueryProjectOwner.select(joinDatasetRequest.get(NewDatasetRequestEntity.FIELD_ID));
			subqueryProjectOwner.where(subRoot.get(ProjectEntity.FIELD_OWNER_UUID).in(searchCriteria.getProjectOwnerUuids()));
			predicates.add(root.get(ProjectEntity.FIELD_ID).in(subqueryProjectOwner));
		}

		if (CollectionUtils.isNotEmpty(searchCriteria.getStatus())) {
			List<NewDatasetRequestStatus> statusList = searchCriteria.getStatus().stream().map(status -> getEntityStatus(status.name())).collect(Collectors.toList());
			predicates.add(root.get(NewDatasetRequestEntity.FIELD_NEW_DATASET_REQUEST_STATUS).in(statusList));
		}
	}

	private static org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestStatus getEntityStatus(String name) {
		return org.rudi.microservice.projekt.storage.entity.newdatasetrequest.NewDatasetRequestStatus.valueOf(name);
	}


}