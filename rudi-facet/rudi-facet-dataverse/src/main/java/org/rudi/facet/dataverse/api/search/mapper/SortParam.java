package org.rudi.facet.dataverse.api.search.mapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SortParam {
	public final String field;
	public final Order order;

	@RequiredArgsConstructor
	public enum Order {
		ASC("asc"),
		DESC("desc");

		public final String stringValue;
	}
}
