package org.rudi.microservice.kalim.service.helper.apim;

import java.io.IOException;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.json.JSONException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.test.RudiAssertions;
import org.rudi.facet.kaccess.bean.Media;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;

import lombok.val;

@ExtendWith(MockitoExtension.class)
class APIManagerHelperUnitTest {
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	@InjectMocks
	private APIManagerHelper apiManagerHelper;

	@Nonnull
	private static String getAPIDescriptionPath(String baseFileName) {
		return getPath(baseFileName, ".apidescription.json");
	}

	@Nonnull
	private static String getMediaPath(String baseFileName) {
		return getPath(baseFileName, ".media.json");
	}

	@Nonnull
	private static String getPath(String baseFileName, String extension) {
		return "media/" + baseFileName + extension;
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"file",
			"file-with-connector-parameters"
	})
	void buildAPIDescriptionByMetadataIntegration(final String baseFileName) throws IOException, JSONException {
		val globalId = UUID.fromString("69784084-fdd1-4c28-aac7-306332ff53bb");
		val nodeProvider = new NodeProvider();
		val provider = new Provider()
				.uuid(UUID.fromString("5596b5b2-b227-4c74-a9a1-719e7c1008c7"))
				.code("NODE_STUB");
		val media = jsonResourceReader.read(getMediaPath(baseFileName), Media.class);

		final var apiDescription = apiManagerHelper.buildAPIDescriptionByMetadataIntegration(globalId, nodeProvider, provider, media);

		RudiAssertions.assertThat(apiDescription).isJsonEqualToContentOf(getAPIDescriptionPath(baseFileName));
	}
}
