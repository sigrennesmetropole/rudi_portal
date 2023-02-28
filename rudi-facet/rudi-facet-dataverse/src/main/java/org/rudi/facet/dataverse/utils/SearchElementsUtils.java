package org.rudi.facet.dataverse.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.collections4.IterableUtils;
import org.rudi.facet.dataverse.bean.SearchDatasetInfo;
import org.rudi.facet.dataverse.model.search.SearchElements;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;

public final class SearchElementsUtils {

	private SearchElementsUtils() {
	}

	@SuppressWarnings({
			"java:S4276", // On laisse Function au lieu de IntFunction car on doit pouvoir passer des références de méthodes
	})
	public static List<SearchDatasetInfo> fetchAllElementsUsing(Function<Integer, SearchElements<SearchDatasetInfo>> pageFetcher) {
		final var flux = page(0, pageFetcher.apply(0))
				.expand(page -> {
					final var nextOffset = page.offset + page.searchElements.getItems().size();
					if (nextOffset < page.searchElements.getTotal()) {
						return page(nextOffset, pageFetcher.apply(nextOffset));
					} else {
						return Mono.empty();
					}
				})
				.flatMap(page -> {
					final var pageContent = page.searchElements.getItems();
					return Flux.fromIterable(Objects.requireNonNullElse(pageContent, Collections.emptyList()));
				});
		return IterableUtils.toList(flux.toIterable());
	}

	private static Mono<Page> page(int offset, SearchElements<SearchDatasetInfo> searchElements) {
		return Mono.just(new Page(offset, searchElements));
	}

	@RequiredArgsConstructor
	private static class Page {
		final int offset;
		final SearchElements<SearchDatasetInfo> searchElements;
	}
}
