package org.rudi.facet.dataverse.helper.query;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.fields.FieldSpec;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
class ItemBuilder<T> {

	static final String ANY_VALUE = "*";

	/**
	 * https://solr.apache.org/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-EscapingSpecialCharacters
	 */
	static final String[] SPECIAL_CHARACTERS = {
			"+",
			"-",
			"&&",
			"||",
			"!",
			"(",
			")",
			"{",
			"}",
			"[",
			"]",
			"^",
			"\"",
			"~",
			"*",
			"?",
			":",
			"/"
	};

	/**
	 * https://solr.apache.org/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-EscapingSpecialCharacters
	 */
	static final String[] ESCAPED_SPECIAL_CHARACTERS = Arrays.stream(SPECIAL_CHARACTERS)
			.map(specialCharacter -> "\\" + specialCharacter)
			.toArray(String[]::new);

	protected final T value;
	private boolean withWildcard = false;
	private boolean withExactMatch = false;

	public String buildForField(FieldSpec fieldSpec) {
		final String filterQueryForExistingField = buildFilterQuery(fieldSpec.getName());
		if (isDefaultValue(fieldSpec)) {
			final String filterQueryForMissingField = buildFilterQueryForMissingField(fieldSpec);
			return filterQueryForExistingField + " OR " + filterQueryForMissingField;
		} else {
			return filterQueryForExistingField;
		}
	}

	protected boolean isDefaultValue(FieldSpec fieldSpec) {
		return Objects.equals(value, fieldSpec.getDefaultValueIfMissing());
	}

	@Nonnull
	private String buildFilterQuery(String field) {
		return field + ":" + valueToString();
	}

	/**
	 * source : https://stackoverflow.com/a/28859224
	 */
	@Nonnull
	private String buildFilterQueryForMissingField(FieldSpec fieldSpec) {
		return "(*:* NOT " + fieldSpec.getName() + ":*)";
	}

	protected String valueToString() {
		return valueToString(value);
	}

	protected <V> String valueToString(@Nonnull V value) {
		return Stream.of(value)
				.map(Object::toString)
				.map(this::escapeSpecialCharacters)
				.map(escapedStringValue -> {
					if (withWildcard) {
						return ANY_VALUE + escapedStringValue + ANY_VALUE;
					} else if (needQuotes()) {
						return "\"" + escapedStringValue + "\"";
					} else {
						return escapedStringValue;
					}
				})
				.collect(Collectors.toList())
				.get(0);
	}

	/**
	 * https://solr.apache.org/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-EscapingSpecialCharacters
	 */
	@Nonnull
	private String escapeSpecialCharacters(@Nonnull String originalValue) {
		if (originalValue.equals(ANY_VALUE)) {
			return originalValue;
		}
		return StringUtils.replaceEach(originalValue, SPECIAL_CHARACTERS, ESCAPED_SPECIAL_CHARACTERS);
	}

	protected boolean needQuotes() {
		return withExactMatch;
	}

	void withWildcard() {
		withWildcard = true;
	}

	void withExactMatch() {
		withExactMatch = true;
	}
}
