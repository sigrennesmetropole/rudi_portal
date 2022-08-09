package org.rudi.common.service.util;

import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

/**
 * UtilPageable pour la couche service
 */
@Component
public class PageableUtil {
	public <T> Page<T> filter(Page<T> page, Predicate<? super T> predicate) {
		if (page.getTotalElements() == 0) {
			return page;
		}

		final var content = page.getContent();
		final var filteredContent = content.stream()
				.filter(predicate)
				.collect(Collectors.toList());
		return new PageImpl<>(filteredContent, page.getPageable(), filteredContent.size());
	}
}
