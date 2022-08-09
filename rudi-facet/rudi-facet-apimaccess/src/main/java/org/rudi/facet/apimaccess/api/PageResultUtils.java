package org.rudi.facet.apimaccess.api;

import org.apache.commons.collections4.IterableUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public final class PageResultUtils {

	private PageResultUtils() {
	}

	@SuppressWarnings({
			"java:S4276", // On laisse Function au lieu de IntFunction car on doit pouvoir passer des références de méthodes
	})
	public static <P, T> List<T> fetchAllElementsUsing(Function<Integer, Mono<P>> pageFetcher, Function<P, List<T>> pageContentGetter, Function<P, Integer> nextFunction) {
		final var flux = pageFetcher.apply(0)
				.expand(page -> {
					final var nextOffset = nextFunction.apply(page);
					if (nextOffset != null) {
						return pageFetcher.apply(nextOffset);
					} else {
						return Mono.empty();
					}
				})
				.flatMap(page -> {
					final var pageContent = pageContentGetter.apply(page);
					return Flux.fromIterable(Objects.requireNonNullElse(pageContent, Collections.emptyList()));
				});
		return IterableUtils.toList(flux.toIterable());
	}
}
