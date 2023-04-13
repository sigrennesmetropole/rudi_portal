package org.rudi.microservice.konsent.storage.dao.treatmentversion.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.konsent.core.bean.TreatmentVersionSearchCriteria;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.TreatmentVersionCustomDao;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import lombok.val;
import static org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity.FIELD_STATUS;
import static org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity.FIELD_VERSION;

@Repository
public class TreatmentVersionCustomDaoImpl extends AbstractCustomDaoImpl<TreatmentVersionEntity, TreatmentVersionSearchCriteria> implements TreatmentVersionCustomDao {
	private static final String FIELD_TREATMENT_UUID = "uuid";
	private static final String FIELD_ID = "id";

	public TreatmentVersionCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, TreatmentVersionEntity.class);
	}

	@Override
	public Page<TreatmentVersionEntity> searchTreatmentVersions(TreatmentVersionSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(TreatmentVersionSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<TreatmentVersionEntity> root, List<Predicate> predicates) {
		if (searchCriteria.getTreatmentUuid() != null) {
			Subquery<Long> subqueryTreatmentVersion = criteriaQuery.subquery(Long.class);
			Root<TreatmentEntity> subRoot = subqueryTreatmentVersion.from(TreatmentEntity.class);
			Join<TreatmentEntity, TreatmentVersionEntity> joinVersion = subRoot.join(FIELD_VERSION, JoinType.INNER);
			subqueryTreatmentVersion.select(joinVersion.get(FIELD_ID)); // Selection des id des Version ?
			subqueryTreatmentVersion.where(builder.equal(subRoot.get(FIELD_TREATMENT_UUID), searchCriteria.getTreatmentUuid()));
			predicates.add(root.get(FIELD_ID).in(subqueryTreatmentVersion));
		}
		if (searchCriteria.getStatus() != null) {
			val statusValidated = TreatmentStatus.valueOf(searchCriteria.getStatus().name()); // Conversion de TreatmentStatus des DTO => TreatmentStatus des Entity
			predicates.add(builder.equal(root.get(FIELD_STATUS), statusValidated));
		}
	}
}
