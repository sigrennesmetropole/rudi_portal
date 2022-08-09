package org.rudi.common.service.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

public abstract class SearchCriteriaMapper<C, T> {
	private final Map<Function<C, String>, Function<String, Predicate<T>>> stringMappings = new HashMap<>();

	protected void mapStringCriterion(Function<C, String> criterionGetter, Function<String, Predicate<T>> predicateBuilder) {
		stringMappings.put(criterionGetter, predicateBuilder);
	}

	public Predicate<T> searchCriteriaToPredicate(C searchCriteria) {
		final AtomicReference<Predicate<T>> predicateReference = new AtomicReference<>(t -> true);

		stringMappings.forEach((criterionGetter, predicateBuilder) -> {
			final var criterionValue = criterionGetter.apply(searchCriteria);
			if (StringUtils.isNotEmpty(criterionValue)) {
				predicateReference.getAndUpdate(predicate -> predicate.and(predicateBuilder.apply(criterionValue)));
			}
		});

		return predicateReference.get();
	}

}
