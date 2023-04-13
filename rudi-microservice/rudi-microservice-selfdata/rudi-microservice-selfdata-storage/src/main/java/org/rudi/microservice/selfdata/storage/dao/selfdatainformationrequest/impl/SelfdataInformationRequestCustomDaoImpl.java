package org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestCustomDao;
import org.rudi.microservice.selfdata.storage.dao.selfdatainformationrequest.SelfdataInformationRequestCustomSearchCriteria;
import org.rudi.microservice.selfdata.storage.entity.selfdatainformationrequest.SelfdataInformationRequestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.val;
import static org.rudi.microservice.selfdata.storage.RepositoryConstants.CREATION_DATE_FIELD;
import static org.rudi.microservice.selfdata.storage.RepositoryConstants.DATASET_UUID_FIELD;
import static org.rudi.microservice.selfdata.storage.RepositoryConstants.INITATOR_FIELD;

@Repository
public class SelfdataInformationRequestCustomDaoImpl extends AbstractCustomDaoImpl<SelfdataInformationRequestEntity, SelfdataInformationRequestCustomSearchCriteria> implements SelfdataInformationRequestCustomDao {

	public SelfdataInformationRequestCustomDaoImpl(EntityManager entityManager) {
		super(entityManager, SelfdataInformationRequestEntity.class);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Page<SelfdataInformationRequestEntity> searchSelfdataInformationRequests(
			SelfdataInformationRequestCustomSearchCriteria searchCriteria, Pageable pageable) {
		return search(searchCriteria, pageable);
	}

	@Override
	protected void addPredicates(SelfdataInformationRequestCustomSearchCriteria searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<SelfdataInformationRequestEntity> root, List<Predicate> predicates) {
		if (StringUtils.isNotBlank(searchCriteria.getLogin())) {
			Predicate loginEqualsCriteriaLogin = builder.equal(root.get(INITATOR_FIELD), searchCriteria.getLogin());
			predicates.add(loginEqualsCriteriaLogin);
		}

		if (searchCriteria.getDatasetUuid() != null) {
			Predicate datasetUuidEqualsCriteriaDatasetUuid = builder.equal(root.get(DATASET_UUID_FIELD), searchCriteria.getDatasetUuid());
			predicates.add(datasetUuidEqualsCriteriaDatasetUuid);
		}
	}

	@Override
	public SelfdataInformationRequestEntity getLastSelfdataInformationRequest(String userLogin, UUID datasetUuid) {

		// Requête de recherche
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);
		// Prédicats de recherche
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(searchRoot.get(INITATOR_FIELD), userLogin));
		predicates.add(builder.equal(searchRoot.get(DATASET_UUID_FIELD), datasetUuid));
		// Application des prédicats et tri voulu
		searchQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
		javax.persistence.criteria.Order order = builder.desc(searchRoot.get(CREATION_DATE_FIELD));
		searchQuery.select(searchRoot)
				.orderBy(order);

		TypedQuery<SelfdataInformationRequestEntity> typedQuery = entityManager.createQuery(searchQuery);
		var result = typedQuery.setMaxResults(1).getResultList();
		if (CollectionUtils.isNotEmpty(result)) {
			return result.get(0);
		}
		return null;
	}
}
