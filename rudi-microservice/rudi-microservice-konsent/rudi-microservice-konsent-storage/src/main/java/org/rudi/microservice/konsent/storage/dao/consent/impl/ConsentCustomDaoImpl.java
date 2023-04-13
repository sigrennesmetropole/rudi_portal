package org.rudi.microservice.konsent.storage.dao.consent.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.common.storage.dao.PredicateListBuilder;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.storage.dao.consent.ConsentCustomDao;
import org.rudi.microservice.konsent.storage.entity.common.OwnerType;
import org.rudi.microservice.konsent.storage.entity.consent.ConsentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import lombok.val;
import static org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity.FIELD_OWNER_TYPE;

@Repository
public class ConsentCustomDaoImpl extends AbstractCustomDaoImpl<ConsentEntity, ConsentSearchCriteria>
		implements ConsentCustomDao {
	private static final String FIELD_CONSENT_DATE = "consentDate";
	private static final String FIELD_EXPIRATION_DATE = "expirationDate";
	private static final String FIELD_OWNER_UUID = "ownerUuid";
	private static final String FIELD_TREATMENT = "treatment";
	private static final String FIELD_UUID = "uuid";

	public ConsentCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, ConsentEntity.class);
	}

	@Override
	public Page<ConsentEntity> searchConsents(ConsentSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	public Page<ConsentEntity> searchMyConsents(ConsentSearchCriteria searchCriteria, Pageable pageable) {
		return internalSearchMyConsents(searchCriteria, pageable);
	}

	private Page<ConsentEntity> internalSearchMyConsents(ConsentSearchCriteria searchCriteria, Pageable pageable) {
		final Long totalCount = getTotalCount(searchCriteria);
		if (totalCount == 0) {
			return emptyPage(pageable);
		}
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);
		val predicates = addPredicatesForMyConsents(builder, searchRoot, searchCriteria);
		searchQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		searchQuery.select(searchRoot).orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));
		val typedQuery = entityManager.createQuery(searchQuery);
		if (pageable.isPaged()) {
			typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
		}
		val myConsentEntities = typedQuery.getResultList();
		return new PageImpl<>(myConsentEntities, pageable, totalCount.intValue());
	}

	private Long getTotalCount(ConsentSearchCriteria searchCriteria) {
		val builder = entityManager.getCriteriaBuilder();
		val countQuery = builder.createQuery(Long.class);
		val countRoot = countQuery.from(entitiesClass);
		val predicates = addPredicatesForMyConsents(builder, countRoot, searchCriteria);
		countQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		countQuery.select(builder.countDistinct(countRoot));
		return entityManager.createQuery(countQuery).getSingleResult();
	}

	private List<Predicate> addPredicatesForMyConsents(CriteriaBuilder builder, Root<ConsentEntity> root,
			ConsentSearchCriteria searchCriteria) {
		val joinTreatment = root.join(FIELD_TREATMENT);
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get(FIELD_OWNER_UUID), searchCriteria.getUserUuids().get(0))); // Que mes consentements
		if (searchCriteria.getTreatmentOwnerUuids() != null) {
			predicates.add(joinTreatment.get(FIELD_OWNER_UUID).in(searchCriteria.getTreatmentOwnerUuids())); // Mes consentements pour un proprio de Treatment donné
		}
		if (searchCriteria.getTreatmentUuids() != null) {
			predicates.add(joinTreatment.get(FIELD_UUID).in(searchCriteria.getTreatmentUuids())); // Mes consentements sur un Treatment donné
		}
		// Gestion des dates
		addDatesPredicates(builder, root, predicates, searchCriteria);

		return predicates;
	}

	private void addDatesPredicates(CriteriaBuilder builder, Root<ConsentEntity> root, List<Predicate> predicates, ConsentSearchCriteria searchCriteria) {
		// Gestion des dates
		if (searchCriteria.getAcceptDateMin() != null) {
			predicates
					.add(builder.greaterThanOrEqualTo(root.get(FIELD_CONSENT_DATE), searchCriteria.getAcceptDateMin()));
		}
		if (searchCriteria.getAcceptDateMax() != null) {
			predicates.add(builder.lessThan(root.get(FIELD_CONSENT_DATE), searchCriteria.getAcceptDateMax()));
		}
		if (searchCriteria.getExpirationDateMin() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(FIELD_EXPIRATION_DATE),
					searchCriteria.getExpirationDateMin()));
		}
		if (searchCriteria.getExpirationDateMax() != null) {
			predicates.add(builder.lessThan(root.get(FIELD_EXPIRATION_DATE), searchCriteria.getExpirationDateMax()));
		}
	}

	@Override
	protected void addPredicates(PredicateListBuilder<ConsentEntity, ConsentSearchCriteria> builder) {
		val searchCriteria = builder.getSearchCriteria();
		builder.add(searchCriteria.getOwnerUuids(),
						((consentEntityRoot, ownerUuids) -> consentEntityRoot.get(FIELD_OWNER_UUID).in(ownerUuids)))
				.add(searchCriteria.getTreatmentUuids(),
						((consentEntityRoot, treatmentUuids) -> consentEntityRoot.join(FIELD_TREATMENT).get(FIELD_UUID)
								.in(treatmentUuids)))
				.add(searchCriteria.getTreatmentOwnerUuids(),
						((consentEntityRoot, treatmentOwnerUuids) -> consentEntityRoot.join(FIELD_TREATMENT)
								.get(FIELD_OWNER_UUID).in(treatmentOwnerUuids)));
	}

	@Override
	protected void addPredicates(ConsentSearchCriteria searchCriteria, CriteriaBuilder builder,
			CriteriaQuery<?> criteriaQuery, Root<ConsentEntity> root, List<Predicate> predicates) {
		// Predicats gérant les critères implicites sur les traitements (user ayant créé le traitement ou appartenant à l'organisation ayant créée le
		// traitement)
		final Join<Object, Object> joinTreatment = root.join(FIELD_TREATMENT);
		Predicate ownerUser = null;
		if (CollectionUtils.isNotEmpty(searchCriteria.getUserUuids())) {
			val treatmentIsCreatedByUser = builder.equal(joinTreatment.get(FIELD_OWNER_TYPE), OwnerType.USER);
			val ownerUuidIsInUserList = joinTreatment.get(FIELD_OWNER_UUID).in(searchCriteria.getUserUuids());
			ownerUser = builder.and(treatmentIsCreatedByUser, ownerUuidIsInUserList);
		}
		Predicate ownerOrganizaton = null;
		if (CollectionUtils.isNotEmpty(searchCriteria.getMyOrganizationsUuids())) {
			val treatmentIsCreatedByOrganization = builder.equal(joinTreatment.get(FIELD_OWNER_TYPE),
					OwnerType.ORGANIZATION);
			val ownerUuidIsInOrganizationList = joinTreatment.get(FIELD_OWNER_UUID)
					.in(searchCriteria.getMyOrganizationsUuids());
			ownerOrganizaton = builder.and(treatmentIsCreatedByOrganization, ownerUuidIsInOrganizationList);
		}
		if (ownerOrganizaton != null && ownerUser != null) {
			predicates.add(builder.or(ownerOrganizaton, ownerUser));
		} else if (ownerOrganizaton != null) {
			predicates.add(ownerOrganizaton);
		} else if (ownerUser != null) {
			predicates.add(ownerUser);
		}
		addDatesPredicates(builder, root, predicates, searchCriteria);
	}
}
