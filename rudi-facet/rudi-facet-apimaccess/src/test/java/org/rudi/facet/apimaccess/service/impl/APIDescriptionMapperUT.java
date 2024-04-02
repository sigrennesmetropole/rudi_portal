package org.rudi.facet.apimaccess.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.rudi.common.test.RudiAssertions.assertThat;

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.json.JSONException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.apimaccess.bean.LimitingPolicies;
import org.rudi.facet.apimaccess.bean.LimitingPolicy;
import org.rudi.facet.apimaccess.helper.api.AdditionalPropertiesHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@ExtendWith(MockitoExtension.class)
class APIDescriptionMapperUT {

	@InjectMocks
	private APIDescriptionMapper apiDescriptionMapper;
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();

	@Mock
	private AdditionalPropertiesHelper additionalPropertiesHelper;

	@ParameterizedTest
	@ValueSource(strings = { "file", "file-with-additionalProperties", "service-dwnl", })
	void buildAPIToSave(final String baseFileName) throws JSONException, IOException {
		final var limitingPolicies = new LimitingPolicies().count(1)
				._list(Collections.singletonList(new LimitingPolicy()));

		when(additionalPropertiesHelper.getAdditionalPropertiesMapAsList(any())).thenCallRealMethod();

		final String[] apiCategories = { "RUDI" };

		final var apiDescription = jsonResourceReader.read(getAPIDescriptionPath(baseFileName), APIDescription.class);
		final var api = apiDescriptionMapper.map(apiDescription, limitingPolicies, apiCategories);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(api);

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
