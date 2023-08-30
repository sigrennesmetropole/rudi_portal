package org.rudi.common.storage.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;

import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @param <E> type des entités manipulées
 * @param <C> type du critère de recherche
 */
@RequiredArgsConstructor
public class AbstractCustomDaoImpl<E, C> {

	protected final EntityManager entityManager;
	protected final Class<E> entitiesClass;

	/**
	 * Ajout d'un prédicat sur la requ
	 *
	 * @param criteria
	 * @param type
	 * @param predicates
	 * @param builder
	 * @param root
	 */
	protected void predicateStringCriteria(String criteria, String type, List<Predicate> predicates,
			CriteriaBuilder builder, From<?, ?> root) {
		if (criteria != null) {
			predicates.add(buildPredicateStringCriteria(criteria, type, builder, root));
		}
	}

	protected void predicateStringCriteria(List<String> criterias, String type, List<Predicate> predicates,
			CriteriaBuilder builder, From<?, ?> root) {
		if (CollectionUtils.isNotEmpty(criterias)) {
			List<Predicate> predicateOrList = new ArrayList<>();
			for (String criteria : criterias) {
				predicateOrList.add(buildPredicateStringCriteria(criteria, type, builder, root));
			}
			predicates.add(builder.or(predicateOrList.toArray(Predicate[]::new)));
		}
	}

	protected <T extends Enum<T>> void predicateStringCriteria(T criteria, String type, List<Predicate> predicates,
			CriteriaBuilder builder, From<?, ?> root) {
		if (criteria != null) {
			predicates.add(buildPredicateStringCriteria(criteria.toString(), type, builder, root));
		}
	}

	protected void predicateStringCriteria(UUID criteria, String type, List<Predicate> predicates,
			CriteriaBuilder builder, From<?, ?> root) {
		if (criteria != null) {
			predicates.add(buildPredicateStringCriteria(criteria, type, builder, root));
		}
	}

	protected void predicateStringCriteriaForJoin(String criteria, String type, List<Predicate> predicates,
			CriteriaBuilder builder, Join<?, ?> join) {
		if (criteria != null) {
			if (criteria.indexOf('*') == -1) {
				predicates.add(builder.equal(join.get(type), criteria));
			} else {
				predicates.add(builder.like(join.get(type), criteria.replace("*", "%")));
			}
		}
	}

	protected void predicateDateCriteriaGreaterThan(LocalDateTime date, String type, List<Predicate> predicates,
			CriteriaBuilder builder, Root<?> root) {
		if (date != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(type), date));
		}
	}

	protected void predicateDateCriteriaLessThan(LocalDateTime date, String type, List<Predicate> predicates,
			CriteriaBuilder builder, Root<?> root) {
		if (date != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get(type), date));
		}
	}

	protected void predicateDateCriteria(LocalDateTime dateDebut, LocalDateTime dateFin, String type,
			List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {

		predicateBetweenCriteria(dateDebut, dateFin, type, predicates, builder, root);
	}

	protected void predicateBooleanCriteria(Boolean criteria, String type, List<Predicate> predicates,
			CriteriaBuilder builder, Root<?> root) {

		if (Boolean.TRUE.equals(criteria)) {
			predicates.add(builder.isTrue(root.get(type)));
		} else {
			predicates.add(builder.isFalse(root.get(type)));
		}

	}

	protected void predicateCriteriaNullOrNot(Boolean criteria, String type, List<Predicate> predicates,
			CriteriaBuilder builder, Root<?> root) {

		if (Boolean.TRUE.equals(criteria)) {
			predicates.add(builder.isNull(root.get(type)));
		} else {
			predicates.add(builder.isNotNull(root.get(type)));
		}

	}

	protected void predicateBooleanOrGreaterThanIntegerCriteria(Boolean criteria, Integer lowerValue,
			List<String> types, List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {
		if (Boolean.TRUE.equals(criteria)) {
			Predicate[] predicatesGreater = types.stream().map(type -> builder.greaterThan(root.get(type), lowerValue))
					.toArray(Predicate[]::new);
			predicates.add(builder.or(predicatesGreater));
		} else {
			Predicate[] predicatesLess = types.stream()
					.map(type -> builder.lessThanOrEqualTo(root.get(type), lowerValue)).toArray(Predicate[]::new);
			predicates.add(builder.and(predicatesLess));
		}
	}

	protected void predicateYearCriteria(Integer anneeDebut, Integer anneeFin, String type, List<Predicate> predicates,
			CriteriaBuilder builder, Root<?> root) {

		predicateBetweenCriteria(anneeDebut, anneeFin, type, predicates, builder, root);

	}

	private <T extends Comparable<? super T>> void predicateBetweenCriteria(T lower, T upper, String type,
			List<Predicate> predicates, CriteriaBuilder builder, Root<?> root) {

		if (lower != null && upper != null) {

			predicates.add(builder.between(root.get(type), lower, upper));

		} else if (lower != null) {

			predicates.add(builder.greaterThanOrEqualTo(root.get(type), lower));

		} else if (upper != null) {

			predicates.add(builder.lessThanOrEqualTo(root.get(type), upper));

		}
	}

	protected Predicate buildPredicateStringCriteria(String criteria, String type, CriteriaBuilder builder,
			From<?, ?> root) {
		if (criteria != null) {
			if (criteria.indexOf('*') == -1) {
				return builder.equal(root.get(type), criteria);
			} else {
				return builder.like(root.get(type), criteria.replace("*", "%"));
			}
		}
		return null;
	}

	protected Predicate buildPredicateStringCriteria(UUID criteria, String type, CriteriaBuilder builder,
			From<?, ?> root) {
		if (criteria != null) {
			return builder.equal(root.get(type), criteria);
		}
		return null;
	}

	protected Page<E> search(@Nullable C searchCriteria, Pageable pageable) {

		if (searchCriteria == null) {
			return emptyPage(pageable);
		}

		final Long totalCount = getTotalCount(entitiesClass, searchCriteria);
		if (totalCount == 0) {
			return emptyPage(pageable);
		}

		// Requête de recherche
		val builder = entityManager.getCriteriaBuilder();
		val searchQuery = builder.createQuery(entitiesClass);
		val searchRoot = searchQuery.from(entitiesClass);
		addWhere(searchCriteria, builder, searchQuery, searchRoot);
		searchQuery.select(searchRoot).distinct(true)
				.orderBy(QueryUtils.toOrders(pageable.getSort(), searchRoot, builder));

		val typedQuery = entityManager.createQuery(searchQuery);
		if (pageable.isPaged()) {
			typedQuery.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
		}
		val projectEntities = typedQuery.getResultList();
		return new PageImpl<>(projectEntities, pageable, totalCount.intValue());
	}

	private Long getTotalCount(Class<E> entitiesClass, C searchCriteria) {
		val builder = entityManager.getCriteriaBuilder();

		val countQuery = builder.createQuery(Long.class);
		val countRoot = countQuery.from(entitiesClass);
		addWhere(searchCriteria, builder, countQuery, countRoot);
		countQuery.select(builder.countDistinct(countRoot));
		return entityManager.createQuery(countQuery).getSingleResult();
	}

	@Nonnull
	protected PageImpl<E> emptyPage(Pageable pageable) {
		return new PageImpl<>(new ArrayList<>(), pageable, 0);
	}

	private void addWhere(C searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery, Root<E> root) {

		if (searchCriteria != null) {
			List<Predicate> predicates = new ArrayList<>();

			addPredicates(searchCriteria, builder, criteriaQuery, root, predicates);

			final PredicateListBuilder<E, C> predicateListBuilder = new PredicateListBuilder<>(searchCriteria,
					predicates, builder, root);
			addPredicates(predicateListBuilder);

			// Définition de la clause Where
			if (CollectionUtils.isNotEmpty(predicates)) {
				criteriaQuery.where(builder.and(predicates.toArray(new Predicate[0])));
			}

		}
	}

	/**
	 * Méthode de configuration des prédicats
	 *
	 * @param searchCriteria le critère de recherche
	 * @param builder        le builder
	 * @param root           la racine de la recherche
	 * @param predicates     la liste des prédicats
	 */
	protected void addPredicates(C searchCriteria, CriteriaBuilder builder, CriteriaQuery<?> criteriaQuery,
			Root<E> root, List<Predicate> predicates) {
		// Par défaut, aucun critère de recherche n'est imposé
	}

	/**
	 * Version de {@link #addPredicates(Object, CriteriaBuilder, CriteriaQuery, Root, List)} utilisant un {@link PredicateListBuilder}
	 */
	protected void addPredicates(PredicateListBuilder<E, C> builder) {
		// Par défaut, aucun critère de recherche n'est imposé
	}

}
