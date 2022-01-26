package org.rudi.common.storage.dao;

import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AbstractCustomDaoImpl {

    /**
     * Ajout d'un prédicat sur la requ
     *
     * @param criteria
     * @param type
     * @param predicates
     * @param builder
     * @param root
     */
    protected void predicateStringCriteria(String criteria, String type, List<Predicate> predicates, CriteriaBuilder builder, From<?, ?> root) {
        if (criteria != null) {
            predicates.add(buildPredicateStringCriteria(criteria, type, builder, root));
        }
    }

    protected void predicateStringCriteria(List<String> criterias, String type, List<Predicate> predicates, CriteriaBuilder builder, From<?, ?> root) {
        if (CollectionUtils.isNotEmpty(criterias)) {
            List<Predicate> predicateOrList = new ArrayList<>();
            for(String criteria: criterias) {
                predicateOrList.add(buildPredicateStringCriteria(criteria, type, builder, root));
            }
            predicates.add(builder.or(predicateOrList.toArray(Predicate[]::new)));
        }
    }

    protected void predicateStringCriteriaForJoin(String criteria, String type, List<Predicate> predicates, CriteriaBuilder builder, Join<?, ?> join) {
        if (criteria != null) {
            if (criteria.indexOf('*') == -1) {
                predicates.add(builder.equal(join.get(type), criteria));
            } else {
                predicates.add(builder.like(join.get(type), criteria.replace("*", "%")));
            }
        }
    }

    protected void predicateDateCriteriaGreaterThan(LocalDateTime date, String type, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {
        if (date != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(type), date));
        }
    }

    protected void predicateDateCriteriaLessThan(LocalDateTime date, String type, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {
        if (date != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(type), date));
        }
    }

    protected void predicateDateCriteria(LocalDateTime dateDebut, LocalDateTime dateFin, String type, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {

        predicateBetweenCriteria(dateDebut, dateFin, type, predicates, builder, root);
    }

    protected void predicateBooleanCriteria(Boolean criteria, String type, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {

        if (Boolean.TRUE.equals(criteria)) {
            predicates.add(builder.isTrue(root.get(type)));
        } else {
            predicates.add(builder.isFalse(root.get(type)));
        }

    }

    protected void predicateCriteriaNullOrNot(Boolean criteria, String type, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {

        if (Boolean.TRUE.equals(criteria)) {
            predicates.add(builder.isNull(root.get(type)));
        } else {
            predicates.add(builder.isNotNull(root.get(type)));
        }

    }

    protected void predicateBooleanOrGreaterThanIntegerCriteria(Boolean criteria, Integer lowerValue, List<String> types, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {
        if (Boolean.TRUE.equals(criteria)) {
            Predicate[] predicatesGreater = types.stream()
                    .map(type -> builder.greaterThan(root.get(type), lowerValue))
                    .toArray(Predicate[]::new);
            predicates.add(builder.or(predicatesGreater));
        } else {
            Predicate[] predicatesLess = types.stream()
                    .map(type -> builder.lessThanOrEqualTo(root.get(type), lowerValue))
                    .toArray(Predicate[]::new);
            predicates.add(builder.and(predicatesLess));
        }
    }

    protected void predicateYearCriteria(Integer anneeDebut, Integer anneeFin, String type, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {

        predicateBetweenCriteria(anneeDebut, anneeFin, type, predicates, builder, root);

    }

    private <T extends Comparable<? super T>> void predicateBetweenCriteria(T lower, T upper, String type, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {

        if (lower != null && upper != null) {

            predicates.add(builder.between(root.get(type), lower, upper));

        } else if (lower != null) {

            predicates.add(builder.greaterThanOrEqualTo(root.get(type), lower));

        } else if (upper != null) {

            predicates.add(builder.lessThanOrEqualTo(root.get(type), upper));

        }
    }

    protected Predicate buildPredicateStringCriteria(String criteria, String type, CriteriaBuilder builder, From<?, ?> root) {
        if (criteria != null) {
            if (criteria.indexOf('*') == -1) {
                return builder.equal(root.get(type), criteria);
            } else {
                return builder.like(root.get(type), criteria.replace("*", "%"));
            }
        }
        return null;
    }

}
