package org.rudi.facet.kaccess.helper.search.mapper.query;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.fields.FieldSpec;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilterQuery extends ArrayList<String> {

	public static final String ANY_VALUE = "*";

	/**
	 * La valeur pour chaque critère ajouté via les méthodes add (par exemple : {@link #add(FieldSpec, Object)})
	 * doit correspondre exactement à la valeur stockée côté Dataverse/Solr
	 */
	private boolean withExactMatch = false;

	public FilterQuery() {
		super();
	}

	public FilterQuery(String... items) {
		super(Arrays.asList(items));
	}

	public static String joinWith(final String separator, final FilterQuery query) {
		if (query.isEmpty()) {
			return StringUtils.EMPTY;
		}
		if (query.size() == 1) {
			return query.get(0);
		}
		return query.stream()
				.map(item -> "(" + item + ")")
				.collect(Collectors.joining(separator));
	}

	public <T> void add(FieldSpec fieldSpec, @Nonnull T value) {
		final ItemBuilder<?> itemBuilder = value instanceof Collection ? new CollectionItemBuilder<>((Collection<?>) value) : new ItemBuilder<>(value);
		if (withExactMatch) {
			itemBuilder.withExactMatch();
		}
		add(itemBuilder.buildForField(fieldSpec));
	}

	public <T> void addWithWildcard(FieldSpec fieldSpec, @Nonnull T value) {
		if (withExactMatch) {
			throw new UnsupportedOperationException("Cannot add wildcarded value when using exactMatch mode");
		}

		final ItemBuilder<?> itemBuilder = value instanceof Collection ? new CollectionItemBuilder<>((Collection<?>) value) : new ItemBuilder<>(value);
		itemBuilder.withWildcard();
		add(itemBuilder.buildForField(fieldSpec));
	}

	public <T> void add(FieldSpec fieldSpec, @Nonnull T minValue, @Nonnull T maxValue) {
		final RangedItemBuilder<?> itemBuilder = new RangedItemBuilder<>(new RangedItemBuilder.RangedValue<>(minValue, maxValue));
		add(itemBuilder.buildForField(fieldSpec));
	}

	public void add(FilterQuery query) {
		super.add(query.toString());
	}

	public String joinWithOr() {
		return joinWith(" OR ", this);
	}

	public String joinWithAnd() {
		return joinWith(" AND ", this);
	}

	/**
	 * La valeur pour chaque critère ajouté via les méthodes add (par exemple : {@link #add(FieldSpec, Object)})
	 * doit correspondre exactement à la valeur stockée côté Dataverse/Solr
	 *
	 * @return this
	 */
	public FilterQuery withExactMatch() {
		this.withExactMatch = true;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof List)) return false;
		if (!super.equals(o)) return false;
		final FilterQuery strings = (FilterQuery) o;
		return withExactMatch == strings.withExactMatch;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), withExactMatch);
	}
}
