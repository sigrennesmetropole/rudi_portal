package org.rudi.facet.dataverse.helper.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.fields.FieldSpec;

public class FilterQuery extends ArrayList<String> {

	public static final String ANY_VALUE = "*";
	private static final String ANY_FIELD = ANY_VALUE;
	private static final String ANY_FIELD_WITH_ANY_VALUE = ANY_FIELD + ":" + ANY_VALUE;
	private static final String OR = " OR ";
	private static final String AND = " AND ";

	/**
	 * La valeur pour chaque critère ajouté via les méthodes add (par exemple : {@link #add(FieldSpec, Object)})
	 * doit correspondre exactement à la valeur stockée côté Dataverse/Solr
	 */
	private boolean withExactMatch = false;

	public FilterQuery() {
		super();
	}

	@Nonnull
	public static String joinWith(final String separator, final FilterQuery query) {
		if (query.isEmpty()) {
			return StringUtils.EMPTY;
		}
		if (query.size() == 1) {
			return query.get(0);
		}
		return query.stream()
				.map(item -> itemNeedsParentheses(item) ? "(" + item + ")" : item)
				.collect(Collectors.joining(separator));
	}

	private static boolean itemNeedsParentheses(String item) {
		return item.contains(AND) || item.contains(OR);
	}

	/**
	 * @param value valeur recherchée, null si on recherche où le champ est sans valeur
	 */
	public <T> FilterQuery add(FieldSpec fieldSpec, @Nullable T value) {
		return add(fieldSpec, value, false);
	}

	public <T> FilterQuery add(FieldSpec fieldSpec, @Nullable T value, boolean except) {
		final String filterQueryItem;
		if (value == null) {
			filterQueryItem = ItemBuilder.buildFilterQueryForFieldWithoutValue(fieldSpec);
		} else {
			final ItemBuilder<?> itemBuilder = value instanceof Collection ? new CollectionItemBuilder<>((Collection<?>) value) : new ItemBuilder<>(value);
			if (withExactMatch) {
				itemBuilder.withExactMatch();
			}
			if (!except) {
				filterQueryItem = itemBuilder.buildForField(fieldSpec);
			} else {
				filterQueryItem = itemBuilder.buildFilterQueryForExceptValueField(fieldSpec);
			}
		}
		add(filterQueryItem);
		return this;
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

	public FilterQuery addAnyFieldWithAnyValue() {
		add(ANY_FIELD_WITH_ANY_VALUE);
		return this;
	}

	/**
	 * Convert this filterQuery to a query with joining items with OR
	 */
	@Nonnull
	public String joinWithOr() {
		return joinWith(OR, this);
	}

	/**
	 * Convert this filterQuery to a query with joining items with AND
	 */
	@Nonnull
	public String joinWithAnd() {
		return joinWith(AND, this);
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
