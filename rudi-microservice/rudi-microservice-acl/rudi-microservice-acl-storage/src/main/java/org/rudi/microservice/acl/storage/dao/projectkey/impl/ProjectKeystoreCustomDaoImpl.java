package org.rudi.microservice.acl.storage.dao.projectkey.impl;

import java.util.ArrayList;
import java.util.List;

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
import org.rudi.microservice.acl.core.bean.ProjectKeystoreSearchCriteria;
import org.rudi.microservice.acl.storage.dao.projectkey.ProjectKeystoreCustomDao;
import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeyEntity;
import org.rudi.microservice.acl.storage.entity.projectkey.ProjectKeystoreEntity;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Repository custom pour les users
 * 
 *
 */
@Repository
public class ProjectKeystoreCustomDaoImpl
		extends AbstractCustomDaoImpl<ProjectKeystoreEntity, ProjectKeystoreSearchCriteria>
		implements ProjectKeystoreCustomDao {

	// Champs utilisés pour le filtrage
	private static final String FIELD_PROJECT_UUID = "projectUuid";
	private static final String FIELD_CLIENT = "client";
	private static final String FIELD_LOGIN = "login";
	private static final String FIELD_ID = "id";
	private static final String FIELD_PROJECT_KEYS = "projectKeys";
	private static final String FIELD_EXPIRATION_DATE = "expirationDate";

	public ProjectKeystoreCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ProjectKeystoreEntity.class);
	}

	@Override
	public Page<ProjectKeystoreEntity> searchUsers(ProjectKeystoreSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(ProjectKeystoreSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<?> criteriaQuery, Root<ProjectKeystoreEntity> root, List<Predicate> predicates) {
		if (CollectionUtils.isNotEmpty(searchCriteria.getProjectUuids())) {
			predicates.add(root.get(FIELD_PROJECT_UUID).in(searchCriteria.getProjectUuids()));
		}
		if (searchCriteria.getMaxExpirationDate() != null || searchCriteria.getMinExpirationDate() != null
				|| StringUtils.isNotEmpty(searchCriteria.getClientId())) {
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<ProjectKeystoreEntity> subRoot = subquery.from(ProjectKeystoreEntity.class);
			Join<ProjectKeystoreEntity, ProjectKeyEntity> joinProjectKeys = subRoot.join(FIELD_PROJECT_KEYS,
					JoinType.INNER);
			subquery.select(joinProjectKeys.get(FIELD_ID));
			List<Predicate> subPredicates = new ArrayList<>();
			if (searchCriteria.getMaxExpirationDate() != null) {
				subPredicates.add(builder.lessThanOrEqualTo(joinProjectKeys.get(FIELD_EXPIRATION_DATE),
						searchCriteria.getMaxExpirationDate()));
			}
			if (searchCriteria.getMinExpirationDate() != null) {
				subPredicates.add(builder.greaterThanOrEqualTo(joinProjectKeys.get(FIELD_EXPIRATION_DATE),
						searchCriteria.getMinExpirationDate()));
			}
			if (StringUtils.isNotEmpty(searchCriteria.getClientId())) {
				Join<ProjectKeyEntity, UserEntity> joinUSer = joinProjectKeys.join(FIELD_CLIENT, JoinType.INNER);
				subPredicates.add(builder.equal(joinUSer.get(FIELD_LOGIN), searchCriteria.getClientId()));
			}
			subquery.where(builder.and(predicates.toArray(new Predicate[0])));
			predicates.add(root.get(FIELD_ID).in(subquery));

		}
	}

}
