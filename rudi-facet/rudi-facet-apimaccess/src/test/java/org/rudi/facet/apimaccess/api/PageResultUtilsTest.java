package org.rudi.facet.apimaccess.api;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.wso2.carbon.apimgt.rest.api.devportal.Pagination;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultUtilsTest {

	@RequiredArgsConstructor
	@Builder
	@Getter
	private static class Page {
		private final int offset;
		@Nullable
		private final List<Element> list;
		@Nullable
		private final Pagination pagination;

		// Le pagination.next des vraies réponses WSO2 est plus compliqué.
		// Par exemple : "/subscriptions?limit=1&offset=1&apiId=032f4d51-f91a-42ff-a45d-1a378c73458a&groupId="
		static Integer next(Page page) {
			if (page.pagination == null) {
				return null;
			}
			final var next = page.pagination.getNext();
			if (StringUtils.isBlank(next)) {
				return null;
			}
			return Integer.valueOf(next);
		}

	}

	private static class Element {

	}

	@Test
	void fetchAllElementsUsingNoPagination() {
		final Page pageWithoutPagination = Page.builder()
				.offset(0)
				.pagination(null)
				.build();
		final Function<Integer, Mono<Page>> pageFetcher = offset -> Mono.just(pageWithoutPagination);

		final var allElements = PageResultUtils.fetchAllElementsUsing(pageFetcher, Page::getList, Page::next);

		assertThat(allElements)
				.as("Empty page should not emit any item")
				.isEmpty();
	}

	@Test
	void fetchAllElementsUsingOnePage() {
		final Element firstElement, secondElement;
		// Pagination extraite d'une vraie réponse WSO2
		final Page singlePage = Page.builder()
				.offset(0)
				.list(Arrays.asList(
						firstElement = new Element(),
						secondElement = new Element()))
				.pagination(new Pagination()
						.offset(0)
						.limit(80)
						.total(2)
						.next("")
						.previous(""))
				.build();
		final Function<Integer, Mono<Page>> pageFetcher = offset -> Mono.just(singlePage);

		final var allElements = PageResultUtils.fetchAllElementsUsing(pageFetcher, Page::getList, Page::next);

		assertThat(allElements)
				.as("We should retrieve all elements from the page")
				.containsExactly(firstElement, secondElement);
	}

	@Test
	void fetchAllElementsUsingTwoPages() {
		final Element firstElement, secondElement, thirdElement;
		// Pagination extraite d'une vraie réponse WSO2
		final Page firstPage = Page.builder()
				.offset(0)
				.list(Arrays.asList(
						firstElement = new Element(),
						secondElement = new Element()))
				.pagination(new Pagination()
						.offset(0)
						.limit(80)
						.total(2)
						.next("")
						.previous(""))
				.build();
		final Page secondPage = Page.builder()
				.offset(1)
				.list(Collections.singletonList(thirdElement = new Element()))
				.pagination(new Pagination()
						.offset(2)
						.limit(80)
						.total(1)
						.next("")
						.previous(""))
				.build();
		//noinspection ConstantConditions
		firstPage.pagination.next(secondPage.offset + "");
		//noinspection ConstantConditions
		secondPage.pagination.previous(firstPage.offset + "");
		final Function<Integer, Mono<Page>> pageFetcher = offset -> Mono.just(offset == 1 ? secondPage : firstPage);

		final var allElements = PageResultUtils.fetchAllElementsUsing(pageFetcher, Page::getList, Page::next);

		assertThat(allElements)
				.as("We should retrieve all elements from the two pages")
				.containsExactly(firstElement, secondElement, thirdElement);
	}
}
