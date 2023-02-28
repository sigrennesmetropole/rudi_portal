package org.rudi.facet.dataverse.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class MetadataListUtils {

	private MetadataListUtils() {
	}

	/**
	 * Récupère les éléments de la première page, puis ceux de la page suivante et ainsi de suite, jusqu'à la dernière page.
	 *
	 * @param pageFetcher       fonction qui récupère une page à partir de son numéro (offset)
	 * @param pageContentGetter fonction qui récupère les éléments d'une page
	 * @param nextFunction      le numéro (offset) de la page suivante, null si aucune page suivante
	 * @param <P>               Type d'une page
	 * @param <T>               Type des éléments dans une page
	 */
	@Nonnull
	@SuppressWarnings({
			"java:S4276", // On laisse Function au lieu de IntFunction car on doit pouvoir passer des références de méthodes
	})
	public static <P, T> Flux<T> fluxAllElementsUsing(Function<Integer, Mono<P>> pageFetcher, Function<P, List<T>> pageContentGetter, Function<P, Integer> nextFunction) {
		return pageFetcher.apply(0)
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
	}
}
