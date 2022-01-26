package org.rudi.microservice.kalim.storage.dao.integration.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.kalim.core.bean.IntegrationRequestSearchCriteria;
import org.rudi.microservice.kalim.storage.dao.integration.IntegrationRequestCustomDao;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class IntegrationRequestCustomDaoImpl extends AbstractCustomDaoImpl implements IntegrationRequestCustomDao {

    private static final String FIELD_TREATMENT_DATE = "treatmentDate";
    private static final String FIELD_SEND_REQUEST_DATE = "sendRequestDate";
    private static final String FIELD_SUBMISSION_DATE = "submissionDate";
    private static final String FIELD_PROGRESS_STATUS = "progressStatus";
    private static final String FIELD_INTEGRATION_STATUS = "integrationStatus";
    private static final String FIELD_GLOBAL_ID = "globalId";

    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<IntegrationRequestEntity> searchIntegrationRequests(IntegrationRequestSearchCriteria searchCriteria,
                                                                    Pageable pageable) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        // Requête pour compter le nombre de resultats total
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<IntegrationRequestEntity> countRoot = countQuery.from(IntegrationRequestEntity.class);
        buildQuery(searchCriteria, builder, countQuery, countRoot);
        countQuery.select(builder.countDistinct(countRoot));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // si aucun resultat
        if (totalCount == 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // Requête de recherche
        CriteriaQuery<IntegrationRequestEntity> searchQuery = builder.createQuery(IntegrationRequestEntity.class);
        Root<IntegrationRequestEntity> searchRoot = searchQuery.from(IntegrationRequestEntity.class);
        buildQuery(searchCriteria, builder, searchQuery, searchRoot);

        searchQuery.select(searchRoot);
        searchQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

        TypedQuery<IntegrationRequestEntity> typedQuery = entityManager.createQuery(searchQuery);

        List<IntegrationRequestEntity> integrationRequestEntities = null;
        if (pageable.isPaged()) {
            integrationRequestEntities = typedQuery.setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize()).getResultList();
        } else {
            integrationRequestEntities = typedQuery.getResultList();
        }

        return new PageImpl<>(integrationRequestEntities, pageable, totalCount.intValue());
    }

    @Override
    public IntegrationRequestEntity findByUUIDAndLock(UUID uuid) {
        IntegrationRequestEntity result = null;
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<IntegrationRequestEntity> searchQuery = builder.createQuery(IntegrationRequestEntity.class);
        Root<IntegrationRequestEntity> searchRoot = searchQuery.from(IntegrationRequestEntity.class);
        searchQuery.where(builder.equal(searchRoot.get("uuid"), uuid));
        TypedQuery<IntegrationRequestEntity> typedQuery = entityManager.createQuery(searchQuery);
        typedQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        List<IntegrationRequestEntity> integrationRequestEntities = typedQuery.getResultList();
        if (CollectionUtils.isNotEmpty(integrationRequestEntities)) {
            result = integrationRequestEntities.get(0);
        }
        return result;
    }

    private void buildQuery(IntegrationRequestSearchCriteria searchCriteria, CriteriaBuilder builder,
                            CriteriaQuery<?> criteriaQuery, Root<IntegrationRequestEntity> root) {

        if (searchCriteria == null) {
            return;
        }
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getGlobalId() != null) {
            predicates.add(builder.equal(root.get(FIELD_GLOBAL_ID), searchCriteria.getGlobalId()));
        }

        if (searchCriteria.getIntegrationStatus() != null) {
            predicates
                    .add(builder.equal(root.get(FIELD_INTEGRATION_STATUS), searchCriteria.getIntegrationStatus()));
        }

        if (CollectionUtils.isNotEmpty(searchCriteria.getProgressStatus())) {
            predicates.add(root.get(FIELD_PROGRESS_STATUS).in(searchCriteria.getProgressStatus()));
        }

        if (searchCriteria.getCreationDateMin() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(FIELD_SUBMISSION_DATE),
                    searchCriteria.getCreationDateMin()));
        }
        if (searchCriteria.getCreationDateMax() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(FIELD_SUBMISSION_DATE),
                    searchCriteria.getCreationDateMax()));
        }

        if (searchCriteria.getTreatmentDateMin() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(FIELD_TREATMENT_DATE),
                    searchCriteria.getTreatmentDateMin()));
        }
        if (searchCriteria.getTreatmentDateMax() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(FIELD_TREATMENT_DATE),
                    searchCriteria.getTreatmentDateMax()));
        }

        if (searchCriteria.getSendRequestDateMin() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(FIELD_SEND_REQUEST_DATE),
                    searchCriteria.getSendRequestDateMin()));
        }
        if (searchCriteria.getSendRequestDateMax() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(FIELD_SEND_REQUEST_DATE),
                    searchCriteria.getSendRequestDateMax()));
        }

        // Définition de la clause Where
        if (CollectionUtils.isNotEmpty(predicates)) {
            criteriaQuery.where(builder.and(predicates.toArray(new Predicate[predicates.size()])));
        }


    }

}
