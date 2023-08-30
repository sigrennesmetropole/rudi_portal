package org.rudi.microservice.strukture.storage.dao.organizationmember.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.storage.dao.organizationmember.OrganizationMemberCustomDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.val;

@Repository
public class OrganizationMemberCustomDaoImpl extends AbstractCustomDaoImpl<OrganizationMemberEntity, OrganizationMembersSearchCriteria> implements OrganizationMemberCustomDao {
	private static final String FIELD_UUID = "uuid";
	private static final String FIELD_USER_UUID = "userUuid";

	public OrganizationMemberCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, OrganizationMemberEntity.class);
	}

	@Override
	public Page<OrganizationMemberEntity> searchOrganizationMembers(OrganizationMembersSearchCriteria searchCriteria, Pageable pageable) {

		if (searchCriteria == null) {
			return emptyPage(pageable);
		}
		val totalCount = getTotalCount(searchCriteria);
		if (searchCriteria.getOrganizationUuid() != null) {
			val builder = entityManager.getCriteriaBuilder();
			val query = builder.createQuery(OrganizationMemberEntity.class);
			val root = query.from(OrganizationEntity.class);
			Join<OrganizationEntity, OrganizationMemberEntity> joinMembersEntity = root.join(OrganizationEntity.FIELD_MEMBERS, JoinType.INNER);
			query.select(joinMembersEntity);

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get(FIELD_UUID), searchCriteria.getOrganizationUuid()));
			if(searchCriteria.getRole() != null){
				predicates.add(builder.equal(joinMembersEntity.get(
						OrganizationMemberEntity.FIELD_ROLE), convertOrganizationRole(searchCriteria.getRole())));
			}
			if(searchCriteria.getUserUuid() != null){
				predicates.add(builder.equal(joinMembersEntity.get(
						OrganizationMemberEntity.FIELD_USER_UUID), searchCriteria.getUserUuid()));
			}

			query.where(builder.equal(root.get(FIELD_UUID), searchCriteria.getOrganizationUuid()));
			val typedQuery = entityManager.createQuery(query);
			if (pageable.isPaged()) {
				typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
			}
			val membersEntitiesList = typedQuery.getResultList();
			return new PageImpl<>(membersEntitiesList, pageable, totalCount.intValue());
		}
		return emptyPage(pageable);
	}

	private Long getTotalCount(OrganizationMembersSearchCriteria searchCriteria) {
		val builder = entityManager.getCriteriaBuilder();
		val countQuery = builder.createQuery(Long.class);
		val countRoot = countQuery.from(OrganizationEntity.class);
		Join<OrganizationEntity, OrganizationMemberEntity> joinMembersEntity = countRoot.join(OrganizationEntity.FIELD_MEMBERS, JoinType.INNER);
		countQuery.where(builder.equal(countRoot.get(FIELD_UUID), searchCriteria.getOrganizationUuid()));
		countQuery.select(builder.count(joinMembersEntity.get(FIELD_USER_UUID)));
		return entityManager.createQuery(countQuery).getSingleResult();
	}

	private OrganizationRole convertOrganizationRole(org.rudi.microservice.strukture.core.bean.OrganizationRole role){
		return OrganizationRole.valueOf(role.getValue());
	}
}
