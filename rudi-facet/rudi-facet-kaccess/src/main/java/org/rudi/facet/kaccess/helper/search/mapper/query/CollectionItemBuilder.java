package org.rudi.facet.kaccess.helper.search.mapper.query;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.stream.Collectors;

class CollectionItemBuilder<T> extends ItemBuilder<Collection<T>> {

	public CollectionItemBuilder(Collection<T> value) {
		super(value);
	}

	@Override
	protected String valueToString() {
		return "(" +
				value.stream()
						.map(this::valueToString)
						.filter(stringValue -> !StringUtils.isEmpty(stringValue))
						.collect(Collectors.joining(" ")
						) +
				")";
	}

	@Override
	protected boolean needQuotes() {
		return true;
	}
}
