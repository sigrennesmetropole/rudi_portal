package org.rudi.common.test.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.rudi.common.core.util.DateTimeUtils.DEFAULT_ZONE_ID;

class JsonResourceReaderUT {
	private final JsonResourceReader reader = new JsonResourceReader();

	@SuppressWarnings("unused") // objet utilisé pour le test du parsing JSON
	private static class JsonObject {
		@JsonProperty
		private String string;

		@JsonProperty
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		private LocalDateTime localDateTime;

		@JsonProperty
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		private OffsetDateTime offsetDateTime;
	}

	@Test
	void read_string() throws IOException {
		final JsonObject object = reader.read("jsonResourceReader/string.json", JsonObject.class);
		assertThat(object).hasFieldOrPropertyWithValue("string", "value");
	}

	@Test
	void read_date_local() throws IOException {
		final JsonObject object = reader.read("jsonResourceReader/localDateTime.json", JsonObject.class);
		final LocalDateTime expected = LocalDateTime.of(2021, 2, 1, 12, 34, 56, 0);
		assertThat(object).as("La date \"2021-02-01T12:34:56\" est correctement parsée")
				.hasFieldOrPropertyWithValue("localDateTime", expected);
	}

	@Test
	void read_date_offset() throws IOException {
		final JsonObject object = reader.read("jsonResourceReader/offsetDateTime.json", JsonObject.class);
		final var localDateTime = LocalDateTime.of(2021, 2, 1, 12, 34, 56, 0);
		final OffsetDateTime expected = OffsetDateTime.of(localDateTime, DEFAULT_ZONE_ID.getRules().getOffset(localDateTime));
		assertThat(object).as("La date \"2021-02-01T12:34:56+01:00\" est correctement parsée")
				.hasFieldOrPropertyWithValue("offsetDateTime", expected);
	}

	@Test
	void read_fileNotFound() {
		assertThatThrownBy(() -> reader.read("jsonResourceReader/notFound.json", JsonObject.class))
				.as("Le fichier n'est pas trouvé")
				.isInstanceOf(FileNotFoundException.class)
				.hasMessage("Ressource de type JsonObject introuvable dans le classpath à ce chemin : jsonResourceReader/notFound.json");
	}
}
