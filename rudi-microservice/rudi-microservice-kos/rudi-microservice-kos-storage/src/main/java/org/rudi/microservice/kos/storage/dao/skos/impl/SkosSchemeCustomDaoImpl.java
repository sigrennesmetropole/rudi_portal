package org.rudi.microservice.kos.storage.dao.skos.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.rudi.common.storage.dao.AbstractCustomDaoImpl;
import org.rudi.microservice.kos.core.bean.SkosSchemeSearchCriteria;
import org.rudi.microservice.kos.storage.dao.skos.SkosSchemeCustomDao;
import org.rudi.microservice.kos.storage.entity.skos.SkosSchemeEntity;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SkosSchemeCustomDaoImpl extends AbstractCustomDaoImpl<SkosSchemeEntity, SkosSchemeSearchCriteria> implements SkosSchemeCustomDao {

    // Champs utilisés pour le filtrage
    public static final String FIELD_OPENING_DATE = "openingDate";
    public static final String FIELD_CLOSING_DATE = "closingDate";

    public SkosSchemeCustomDaoImpl(EntityManager entityManager) {
        super(entityManager, SkosSchemeEntity.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Page<SkosSchemeEntity> searchSkosSchemes(SkosSchemeSearchCriteria searchCriteria, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        // Requête pour compter le nombre de resultats total
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<SkosSchemeEntity> countRoot = countQuery.from(SkosSchemeEntity.class);
        buildQuery(searchCriteria, builder, countQuery, countRoot);
        countQuery.select(builder.countDistinct(countRoot));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // si aucun resultat

        if (totalCount == 0) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        // Requête de recherche
        CriteriaQuery<SkosSchemeEntity> searchQuery = builder.createQuery(SkosSchemeEntity.class);
        Root<SkosSchemeEntity> searchRoot = searchQuery.from(SkosSchemeEntity.class);
        buildQuery(searchCriteria, builder, searchQuery, searchRoot);

        searchQuery.select(searchRoot);
        searchQuery.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

        TypedQuery<SkosSchemeEntity> typedQuery = entityManager.createQuery(searchQuery);
        List<SkosSchemeEntity> skosSchemeEntities = typedQuery.setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize()).getResultList();
        return new PageImpl<>(skosSchemeEntities, pageable, totalCount.intValue());
    }

    private void buildQuery(SkosSchemeSearchCriteria searchCriteria, CriteriaBuilder builder,
                            CriteriaQuery<?> criteriaQuery, Root<SkosSchemeEntity> root) {

        if (searchCriteria != null) {
            List<Predicate> predicates = new ArrayList<>();

            if (BooleanUtils.isTrue(searchCriteria.getActive())) {
                final LocalDateTime actualDate = LocalDateTime.now();
                predicateDateCriteriaLessThan(actualDate, FIELD_OPENING_DATE, predicates, builder, root);
                predicateDateCriteriaGreaterThan(actualDate, FIELD_CLOSING_DATE, predicates, builder, root);
            }

            // Définition de la clause Where
            if (CollectionUtils.isNotEmpty(predicates)) {
                criteriaQuery.where(builder.and(predicates.toArray(Predicate[]::new)));
            }
        }

    }
}
