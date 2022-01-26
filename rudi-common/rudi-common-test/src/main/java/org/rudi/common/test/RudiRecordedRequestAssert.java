package org.rudi.common.test;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.RecordedRequest;
import org.springframework.http.HttpMethod;

import static org.rudi.common.test.RudiAssertions.assertThat;

public class RudiRecordedRequestAssert extends RudiObjectAssert<RecordedRequest> {
	RudiRecordedRequestAssert(RecordedRequest actual) {
		super(actual);
	}

	public RudiRecordedRequestAssert hasMethod(HttpMethod patch) {
		hasFieldOrPropertyWithValue("method", patch.name());
		return this;
	}

	public RudiRecordedRequestAssert hasPath(String path) {
		hasFieldOrPropertyWithValue("path", path);
		return this;
	}

	public RudiRecordedRequestAssert hasQuery(String query) {
		final HttpUrl requestUrl = actual.getRequestUrl();
		assertThat(requestUrl).as("Request URL is not null").isNotNull();
		//noinspection ConstantConditions l'assertion précédente vérifie déjà que requestUrl est non null
		assertThat(requestUrl.query()).isEqualTo(query);
		return this;
	}
}
