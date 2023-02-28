package org.rudi.facet.dataverse.helper.query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.FieldSpecNamingCase;
import org.rudi.facet.dataverse.fields.RootFieldSpec;

import static org.assertj.core.api.Assertions.assertThat;

class ItemBuilderTest {

	private static final FieldSpec ROOT = new RootFieldSpec(SampleObject.class, "root", FieldSpecNamingCase.SNAKE_CASE);
	private static final FieldSpec FIELD = ROOT.newChildFromJavaField("field")
			.isDirectSortable(false);

	/**
	 * RUDI-961
	 * On échappe les caractères spéciaux Solr
	 * Source : https://solr.apache.org/guide/6_6/the-standard-query-parser.html#TheStandardQueryParser-EscapingSpecialCharacters
	 */
	@ParameterizedTest(name = "{0}")
	@CsvSource({
			"+,\\+",
			"-,\\-",
			"&&,\\&&",
			"||,\\||",
			"!,\\!",
			"(,\\(",
			"),\\)",
			"{,\\{",
			"},\\}",
			"[,\\[",
			"],\\]",
			"^,\\^",
			"\",\\\"",
			"~,\\~",
			"*mot*,\\*mot\\*",
			"?,\\?",
			":,\\:",
			"/,\\/",
	})
	void buildForFieldSpecialCharacters(final String inputValue, final String outputValue) {
		final ItemBuilder<String> builder = new ItemBuilder<>(inputValue);
		assertThat(builder.buildForField(FIELD)).isEqualTo("root_field:" + outputValue);
	}

	/**
	 * RUDI-961
	 * On n'échappe pas les caractères non spéciaux
	 */
	@ParameterizedTest(name = "{0}")
	@CsvSource(value = {
			"&,&",
			"|,|",
			"\\,\\"
	})
	void buildForFieldNotSpecialCharacters(final String inputValue, final String outputValue) {
		final ItemBuilder<String> builder = new ItemBuilder<>(inputValue);
		assertThat(builder.buildForField(FIELD)).isEqualTo("root_field:" + outputValue);
	}

	/**
	 * RUDI-961
	 * On n'échappe pas certains caractères spéciaux lorsqu'ils sont utilisés seuls
	 */
	@ParameterizedTest(name = "{0}")
	@CsvSource(value = {
			"*,*",
	})
	void buildForFieldSpecialCharactersAlone(final String inputValue, final String outputValue) {
		final ItemBuilder<String> builder = new ItemBuilder<>(inputValue);
		assertThat(builder.buildForField(FIELD)).isEqualTo("root_field:" + outputValue);
	}

	/**
	 * RUDI-961
	 * On n'échappe pas les caractères non spéciaux
	 */
	@Test
	void buildForFieldSimpleQuote() {
		final ItemBuilder<String> builder = new ItemBuilder<>("'");
		assertThat(builder.buildForField(FIELD)).isEqualTo("root_field:'");
	}

	private static class SampleObject {
		private String field;
	}
}
