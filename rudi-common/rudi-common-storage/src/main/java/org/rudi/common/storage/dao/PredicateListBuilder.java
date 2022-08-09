package org.rudi.common.storage.dao;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * @param <E> entity type
 * @param <C> search criteria type
 */
@RequiredArgsConstructor
public class PredicateListBuilder<E, C> {

	@Getter
	private final C searchCriteria;
	private final List<Predicate> predicates;
	private final CriteriaBuilder criteriaBuilder;
	private final Root<E> root;

	/**
	 * @return this
	 */
	public <V> PredicateListBuilder<E, C> add(Collection<V> criterionValue, BiFunction<Root<E>, Collection<V>, Predicate> predicateBuilder) {
		if (CollectionUtils.isNotEmpty(criterionValue)) {
			val predicate = predicateBuilder.apply(root, criterionValue);
			predicates.add(criteriaBuilder.and(predicate));
		}
		return this;
	}

	/**
	 * @return this
	 */
	public <V> PredicateListBuilder<E, C> add(V criterionValue, BiFunction<Root<E>, V, Predicate> predicateBuilder) {
		if (criterionValue != null) {
			val predicate = predicateBuilder.apply(root, criterionValue);
			predicates.add(criteriaBuilder.and(predicate));
		}
		return this;
	}

	/**
	 * Ajoute un filtre d'égalité entre un champ de l'entité et une valeur d'enum
	 *
	 * @param criterionValue              valeur de l'enum côté SearchCriteria
	 * @param criteriaToEntityEnumMapping fonction qui converti un enum côté SearchCriteria en enum côté Entity
	 * @param entityGetter                expression JPA pour récupérer le champ correspondant côté Entity
	 * @return this
	 */
	public <A extends Enum<A>, B extends Enum<B>> PredicateListBuilder<E, C> add(A criterionValue, Function<String, B> criteriaToEntityEnumMapping, Function<Root<E>, Expression<E>> entityGetter) {
		if (criterionValue != null) {
			val entityValue = criteriaToEntityEnumMapping.apply(criterionValue.name());
			predicates.add(criteriaBuilder.equal(entityGetter.apply(root), entityValue));
		}
		return this;
	}

	/**
	 * @return this
	 */
	public <A extends Enum<A>, B extends Enum<B>> PredicateListBuilder<E, C> add(A criterionValue, Function<String, B> criteriaToEntityEnumMapping, BiFunction<Root<E>, B, Predicate> predicateBuilder) {
		if (criterionValue != null) {
			final var entityValue = criteriaToEntityEnumMapping.apply(criterionValue.name());
			val predicate = predicateBuilder.apply(root, entityValue);
			predicates.add(criteriaBuilder.and(predicate));
		}
		return this;
	}
	/**
	 * @return this
	 */
	public <A extends Enum<A>, B extends Enum<B>> PredicateListBuilder<E, C> add(Collection<A> criterionValues, Function<String, B> criteriaToEntityEnumMapping, BiFunction<Root<E>, Collection<B>, Predicate> predicateBuilder) {
		if (CollectionUtils.isNotEmpty(criterionValues)) {
			final var entityValues = criterionValues.stream()
					.map(criterionValue -> criteriaToEntityEnumMapping.apply(criterionValue.name()))
					.collect(Collectors.toList());
			val predicate = predicateBuilder.apply(root, entityValues);
			predicates.add(criteriaBuilder.and(predicate));
		}
		return this;
	}

	public void addIsNull(Boolean criterionValue, Function<Root<E>, Expression<E>> entityGetter) {
		if (criterionValue != null) {
			val entityValue = entityGetter.apply(root);

			final Predicate predicate;
			if (Boolean.TRUE.equals(criterionValue)) {
				predicate = criteriaBuilder.isNull(entityValue);
			} else {
				predicate = criteriaBuilder.isNotNull(entityValue);
			}

			predicates.add(predicate);
		}
	}

	public void addIsNotNull(Boolean criterionValue, Function<Root<E>, Expression<E>> entityGetter) {
		if (criterionValue != null) {
			addIsNull(!criterionValue, entityGetter);
		}
	}

}
