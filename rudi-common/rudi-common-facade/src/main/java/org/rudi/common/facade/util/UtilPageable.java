package org.rudi.common.facade.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Component
public class UtilPageable {

	private final int paginationSize;

	public UtilPageable(@Value("${pagination.size:10}") int paginationSize) {
		this.paginationSize = paginationSize;
	}

	/**
	 * @param offset         premier élément de la page
	 * @param limit          nombre d'éléments dans la page (= pagination.size)
	 * @param sortExpression champs utilisés pour le tri, séparés par des virgules, null si non trié
	 */
	public Pageable getPageable(@Nullable Integer offset, @Nullable Integer limit, @Nullable final String sortExpression) {

		if (offset == null || offset < 0) {
			offset = 0;
		}
		if (limit == null || limit <= 0) {
			limit = paginationSize;
		}
		final int page = offset / limit;
		final Sort sort = getSort(sortExpression);
		return PageRequest.of(page, limit, sort);
	}

	@Nonnull
	private Sort getSort(@Nullable String sortExpression) {
		if (StringUtils.isEmpty(sortExpression)) {
			return Sort.unsorted();
		} else {
			final List<Sort.Order> orders = getOrders(sortExpression);
			return Sort.by(orders);
		}
	}

	/**
	 * @param sortExpression champs utilisés pour le tri, séparés par des virgules, préfixés par un "-" si tri descendant
	 * @return tous les tris de champs correspondants
	 */
	private List<Sort.Order> getOrders(@Nonnull final String sortExpression) {

		final List<Sort.Order> listOrders = new ArrayList<>();

		// Get param from sortExpression
		final String[] filter = sortExpression.split(",");

		Sort.Order orders;
		for (final String f : filter) {

			if (f.startsWith("-")) {
				orders = new Sort.Order(Sort.Direction.DESC, f.substring(1));
			} else {
				orders = new Sort.Order(Sort.Direction.ASC, f);
			}
			listOrders.add(orders);
		}
		return listOrders;
	}
}
