package org.rudi.microservice.acl.storage.dao.user.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.acl.core.bean.UserSearchCriteria;
import org.rudi.microservice.acl.storage.dao.user.UserCustomDao;
import org.rudi.microservice.acl.storage.entity.user.UserEntity;
import org.rudi.microservice.acl.storage.entity.user.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository custom pour les users
 * 
 *
 */
@Repository
public class UserCustomDaoImpl extends AbstractCustomDaoImpl implements UserCustomDao {

	// Champs utilisés pour le filtrage
	private static final String FIELD_LOGIN = "login";
	private static final String FIELD_FIRSTNAME = "firstname";
	private static final String FIELD_LASTNAME = "lastname";
	private static final String FIELD_COMPANY = "company";
	private static final String FIELD_TYPE = "type";

	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<UserEntity> searchUsers(UserSearchCriteria searchCriteria, Pageable pageable) {

		CriteriaBuilder builder = entityManager.getCriteriaBuilder();

		// Requête pour compter le nombre de resultats total
		CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
		Root<UserEntity> countRoot = countQuery.from(UserEntity.class);
		buildQuery(searchCriteria, builder, countQuery, countRoot);
		countQuery.select(builder.countDistinct(countRoot));
		Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

		// si aucun resultat

		if (totalCount == 0) {
			return new PageImpl<>(new ArrayList<>(), pageable, 0);
		}

		// Requête de recherche
		CriteriaQuery<UserEntity> searchQuery = builder.createQuery(UserEntity.class);
		Root<UserEntity> searchRoot = searchQuery.from(UserEntity.class);
		buildQuery(searchCriteria, builder, searchQuery, searchRoot);

		searchQuery.select(searchRoot);
		searchQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

		TypedQuery<UserEntity> typedQuery = entityManager.createQuery(searchQuery);
		List<UserEntity> userEntities = typedQuery.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize()).getResultList();
		return new PageImpl<>(userEntities, pageable, totalCount.intValue());
	}

	private void buildQuery(UserSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			Root<UserEntity> root) {

		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();

			// login
			predicateStringCriteria(searchCriteria.getLogin(), FIELD_LOGIN, predicates, builder, root);

			// firstname
			predicateStringCriteria(searchCriteria.getFirstname(), FIELD_FIRSTNAME, predicates, builder, root);

			// lastname
			predicateStringCriteria(searchCriteria.getLastname(), FIELD_LASTNAME, predicates, builder, root);

			// company
			predicateStringCriteria(searchCriteria.getCompany(), FIELD_COMPANY, predicates, builder, root);

			// type
			if (searchCriteria.getType() != null) {
				predicates.add(builder.equal(root.get(FIELD_TYPE), UserType.valueOf(searchCriteria.getType().name())));
			}

			// Définition de la clause Where
			if (CollectionUtils.isNotEmpty(predicates)) {
				criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
			}

		}
	}

}
