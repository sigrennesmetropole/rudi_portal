package org.rudi.facet.kaccess.helper.search.mapper.query;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESOURCE_TITLE;

class ItemBuilderTest {

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
		assertThat(builder.buildForField(RESOURCE_TITLE)).isEqualTo("rudi_resource_title:" + outputValue);
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
		assertThat(builder.buildForField(RESOURCE_TITLE)).isEqualTo("rudi_resource_title:" + outputValue);
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
		assertThat(builder.buildForField(RESOURCE_TITLE)).isEqualTo("rudi_resource_title:" + outputValue);
	}

	/**
	 * RUDI-961
	 * On n'échappe pas les caractères non spéciaux
	 */
	@Test
	void buildForFieldSimpleQuote() {
		final ItemBuilder<String> builder = new ItemBuilder<>("'");
		assertThat(builder.buildForField(RESOURCE_TITLE)).isEqualTo("rudi_resource_title:'");
	}
}
