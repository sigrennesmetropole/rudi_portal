package org.rudi.microservice.konsent.storage.dao.treatment.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.common.storage.dao.PredicateListBuilder;
import org.rudi.microservice.konsent.core.bean.TreatmentSearchCriteria;
import org.rudi.microservice.konsent.storage.dao.treatment.TreatmentsCustomDao;
import org.rudi.microservice.konsent.storage.entity.common.OwnerType;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import lombok.val;
import static org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity.FIELD_OWNER_TYPE;
import static org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity.FIELD_OWNER_UUID;
import static org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity.FIELD_STATUS;
import static org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity.FIELD_VERSION;

@Repository
public class TreatmentsCustomDaoImpl extends AbstractCustomDaoImpl<TreatmentEntity, TreatmentSearchCriteria> implements TreatmentsCustomDao {
	private static final String FIELD_UUID = "uuid";
	private static final String FIELD_PURPOSE = "purpose";
	private static final String FIELD_VERSION_UPDATED_DATE = "updatedDate";


	public TreatmentsCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, TreatmentEntity.class);
	}

	@Override
	public Page<TreatmentEntity> searchTreatments(TreatmentSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(PredicateListBuilder<TreatmentEntity, TreatmentSearchCriteria> builder) {
		val searchCriteria = builder.getSearchCriteria();
		// Conversion de TreatmentStatus des DTO => TreatmentStatus des Entity
		List<TreatmentStatus> treatmentStatusFromRepo = new ArrayList<>();
		for (org.rudi.microservice.konsent.core.bean.TreatmentStatus status : searchCriteria.getTreatmentStatuses()) {
			treatmentStatusFromRepo.add(TreatmentStatus.valueOf(status.name()));
		}
		builder.add(searchCriteria.getPurposes(), (treatmentEntityRoot, purposes) -> treatmentEntityRoot.join(FIELD_VERSION)
						.get(FIELD_PURPOSE).get(FIELD_UUID).in(purposes))
				.add(treatmentStatusFromRepo, (treatmentEntityRoot, treatmentStatuses) -> treatmentEntityRoot
						.get(FIELD_STATUS).in(treatmentStatusFromRepo));
	}

	@Override
	protected void addPredicates(TreatmentSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<TreatmentEntity> root, List<Predicate> predicates) {
		// Predicats gérant les critères implicites sur les traitements (user ayant créé le traitement ou appartenant à l'organisation ayant créée le traitement)
		val treatmentIsCreatedByUser = builder.equal(root.get(FIELD_OWNER_TYPE), OwnerType.USER);
		val ownerUuidIsInUserList = root.get(FIELD_OWNER_UUID).in(searchCriteria.getUserUuids());
		if (CollectionUtils.isNotEmpty(searchCriteria.getMyOrganizationsUuids())) {
			val treatmentIsCreatedByOrganization = builder.equal(root.get(FIELD_OWNER_TYPE), OwnerType.ORGANIZATION);
			val ownerUuidIsInOrganizationList = root.get(FIELD_OWNER_UUID).in(searchCriteria.getMyOrganizationsUuids());
			predicates.add(
					builder.or(
							builder.and(treatmentIsCreatedByUser, ownerUuidIsInUserList),
							builder.and(treatmentIsCreatedByOrganization, ownerUuidIsInOrganizationList)
					)
			);
		} else {
			predicates.add(
					builder.and(treatmentIsCreatedByUser, ownerUuidIsInUserList)
			);
		}
	}

	@Override
	public TreatmentEntity getTreatmentByUuidAndStatus(UUID uuid, Boolean statusIsValidated) {
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(searchRoot.get(FIELD_UUID), uuid));

		if (statusIsValidated) {
			Join<TreatmentEntity, TreatmentVersionEntity> joinVersion = searchRoot.join(FIELD_VERSION);
			predicates.add(builder.equal(joinVersion.get(FIELD_STATUS), TreatmentStatus.VALIDATED));
		}
		searchQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		searchQuery.select(searchRoot);
		searchQuery.orderBy(QueryUtils.toOrders(Sort.by(Sort.Direction.DESC, FIELD_VERSION_UPDATED_DATE), searchRoot, builder));
		TypedQuery<TreatmentEntity> typedQuery = entityManager.createQuery(searchQuery);
		if (typedQuery.getResultList().isEmpty()) {
			return null;
		}
		return typedQuery.getResultList().get(0);
	}

	@Override
	public TreatmentEntity getTreatmentByVersionUuid(UUID treatmentVersionUuid) throws NoResultException {
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);

		List<Predicate> predicates = new ArrayList<>();

		Join<TreatmentEntity, TreatmentVersionEntity> joinVersion = searchRoot.join(FIELD_VERSION);
		predicates.add(builder.equal(joinVersion.get(FIELD_UUID), treatmentVersionUuid));

		searchQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		searchQuery.select(searchRoot);
		TypedQuery<TreatmentEntity> typedQuery = entityManager.createQuery(searchQuery);
		return typedQuery.getSingleResult();
	}
}
