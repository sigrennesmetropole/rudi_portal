package org.rudi.microservice.strukture.storage.dao.address.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.strukture.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.strukture.storage.dao.address.AddressRoleCustomDao;
import org.rudi.microservice.strukture.storage.entity.address.AddressRoleEntity;
import org.rudi.microservice.strukture.storage.entity.address.AddressType;
import org.springframework.data.domain.Sort;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository custom pour les producteurs
 * 
 * @author FNI18300
 *
 */
@Repository
public class AddressRoleCustomDaoImpl extends AbstractCustomDaoImpl<AddressRoleEntity, AddressRoleSearchCriteria> implements AddressRoleCustomDao {

	// Champs utilisés pour le filtrage
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_OPENING_DATE = "openingDate";
	public static final String FIELD_CLOSING_DATE = "closingDate";

	public AddressRoleCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, AddressRoleEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<AddressRoleEntity> searchAddressRoles(AddressRoleSearchCriteria searchCriteria) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AddressRoleEntity> searchQuery = builder.createQuery(AddressRoleEntity.class);
		Root<AddressRoleEntity> searchRoot = searchQuery.from(AddressRoleEntity.class);
		buildQuery(searchCriteria, builder, searchQuery, searchRoot);

		searchQuery.select(searchRoot);
		searchQuery.orderBy(QueryUtils.toOrders(Sort.by("order", "label"), searchRoot, builder));

		TypedQuery<AddressRoleEntity> typedQuery = entityManager.createQuery(searchQuery);
		return typedQuery.getResultList();
	}

	private void buildQuery(AddressRoleSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<?> criteriaQuery, Root<AddressRoleEntity> root) {

		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();

			if (searchCriteria.getType() != null) {
				predicates
						.add(builder.equal(root.get(FIELD_TYPE), AddressType.valueOf(searchCriteria.getType().name())));
			}

			// inactif
			if (Boolean.TRUE.equals(searchCriteria.getActive())) {
				final LocalDateTime d = LocalDateTime.now();
				predicates.add(builder.and(builder.lessThanOrEqualTo(root.get(FIELD_OPENING_DATE), d),
						builder.or(builder.greaterThanOrEqualTo(root.get(FIELD_CLOSING_DATE), d),
								builder.isNull(root.get(FIELD_CLOSING_DATE)))));
			}

			// Définition de la clause Where
			if (CollectionUtils.isNotEmpty(predicates)) {
				criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
			}

		}
	}

}
