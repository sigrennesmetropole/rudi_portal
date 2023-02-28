package org.rudi.facet.apimaccess.service.impl;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.json.JSONException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;

import static org.rudi.common.test.RudiAssertions.assertThat;

@ExtendWith(MockitoExtension.class)
class APIDescriptionMapperTest {

	@InjectMocks
	private APIDescriptionMapper apiDescriptionMapper;
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();

	@ParameterizedTest
	@ValueSource(strings = {
			"file",
			"file-with-additionalProperties",
			"service-dwnl",
	})
	void buildAPIToSave(final String baseFileName) throws JSONException, IOException {
		final var limitingPolicies = new LimitingPolicies()
				.count(1)
				.list(Collections.singletonList(new LimitingPolicy()
				));

		final String[] apiCategories = { "RUDI" };

		final var apiDescription = jsonResourceReader.read(getAPIDescriptionPath(baseFileName), APIDescription.class);
		final var api = apiDescriptionMapper.map(apiDescription, limitingPolicies, apiCategories);

		assertThat(api).isJsonEqualToContentOf(getAPIPath(baseFileName));
	}

	@Nonnull
	private String getAPIDescriptionPath(String baseFileName) {
		return getPath(baseFileName, ".apidescription.json");
	}

	@Nonnull
	private String getAPIPath(String baseFileName) {
		return getPath(baseFileName, ".api.json");
	}

	@Nonnull
	private String getPath(String baseFileName, String extension) {
		return "api/mapper/" + baseFileName + extension;
	}
}
