package org.rudi.common.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.AbstractStringAssert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rudi.common.core.json.DefaultJackson2ObjectMapperBuilder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static java.util.Objects.requireNonNull;

public class JsonAssert<A> extends AbstractStringAssert<JsonAssert<A>> {

	private static final int JSON_INDENT = 2;
	private static final Jackson2ObjectMapperBuilder JACKSON_2_OBJECT_MAPPER_BUILDER = new DefaultJackson2ObjectMapperBuilder();

	public JsonAssert(A actual) throws JsonProcessingException, JSONException {
		super(toJson(actual), JsonAssert.class);
	}

	private static <T> String toJson(T actual) throws JSONException, JsonProcessingException {
		final ObjectMapper mapper = JACKSON_2_OBJECT_MAPPER_BUILDER.build();
		return formatJson(mapper.writeValueAsString(actual));
	}

	private static String formatJson(String json) throws JSONException {
		if (json.strip().startsWith("[")) {
			return new JSONArray(json).toString(JSON_INDENT);
		} else {
			return new JSONObject(json).toString(JSON_INDENT);
		}
	}

	public void isEqualToContentOf(String path) throws IOException, JSONException {
		requireNonNull(path, "Le chemin vers le JSON attendu ne doit pas être null");

		final URL resource = getClass().getClassLoader().getResource(path);
		if (resource == null) {
			throw new FileNotFoundException("Impossible de vérifier l'assertion car le JSON attendu est introuvable dans le classpath à ce chemin : " + path);
		}
		final String expected = IOUtils.toString(resource, StandardCharsets.UTF_8);

		isEqualToJsonString(expected);
	}

	public <T> void isEqualToJsonRepresentationOf(T expectedObject) throws IOException, JSONException {
		final String expectedJsonString = toJson(expectedObject);
		isEqualToJsonString(expectedJsonString);
	}

	private void isEqualToJsonString(String expectedJsonString) throws JSONException {
		try {
			// On utilise cette librairie qui est moins stricte que la comparaison brute entre deux chaînes JSON
			JSONAssert.assertEquals(expectedJsonString, actual, JSONCompareMode.NON_EXTENSIBLE);
		} catch (final AssertionError e) {
			// On relance l'assertion AssertJ pour avoir un message d'erreur complet de JSONAssert tout en gardant le diff expected/actual au format JSON
			as(e.getLocalizedMessage())
					.isEqualTo(formatJson(expectedJsonString));
		}
	}

	// Les méthodes suivantes sont les méthodes de ObjectAssert avec un type de retour = RudiObjectAssert

	@Override
	public JsonAssert<A> as(String description, Object... args) {
		super.as(description, args);
		return this;
	}
}
