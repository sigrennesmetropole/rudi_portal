package org.rudi.common.test;

import org.json.JSONException;

import java.io.IOException;

public class RudiObjectAssert<A> extends org.assertj.core.api.ObjectAssert<A> {

	RudiObjectAssert(A actual) {
		super(actual);
	}

	/**
	 * Verifies that the actual value is equal to expected comparing JSON representations.
	 *
	 * @param path the given file path containing the JSON to compare the actual to.
	 * @return this assertion object.
	 * @throws IOException   if the JSON serializing of the actual value failed
	 * @throws JSONException if the JSON formatting failed
	 */
	// source : #isXmlEqualToContentOf
	@SuppressWarnings("UnusedReturnValue") // On garde le retour pour pouvoir faire des appels chaînés
	public RudiObjectAssert<A> isJsonEqualToContentOf(String path) throws IOException, JSONException {
		final JsonAssert<A> jsonAssert = new JsonAssert<>(actual);
		jsonAssert.isEqualToContentOf(path);
		return this;
	}

	/**
	 * Verifies that the actual value is equal to expected comparing JSON representations.
	 *
	 * @param expectedObject the expected object to compare the actual to.
	 * @return this assertion object.
	 * @throws IOException   if the JSON serializing of the actual value failed
	 * @throws JSONException if the JSON formatting failed
	 */
	@SuppressWarnings("UnusedReturnValue") // On garde le retour pour pouvoir faire des appels chaînés
	public <E extends A> RudiObjectAssert<A> isJsonEqualTo(E expectedObject) throws IOException, JSONException {
		final JsonAssert<A> jsonAssert = new JsonAssert<>(actual);
		jsonAssert.isEqualToJsonRepresentationOf(expectedObject);
		return this;
	}

	// Les méthodes suivantes sont les méthodes de ObjectAssert avec un type de retour = RudiObjectAssert

	@Override
	public RudiObjectAssert<A> as(String description, Object... args) {
		//noinspection ResultOfMethodCallIgnored : on renvoie this
		super.as(description, args);
		return this;
	}
}
