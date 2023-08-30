package org.rudi.facet.kaccess.helper.dataset.metadatablock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.test.RudiAssertions;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.kaccess.KaccessSpringBootTest;
import org.rudi.facet.kaccess.bean.Metadata;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.io.IOException;

@KaccessSpringBootTest
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class MetadataBlockHelperVersionsIT {

	private static final String PERSISTENT_ID = "doi:10.5072/FK2/JV9LCO";

	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final MetadataBlockHelper metadataBlockHelper;

	@Nonnull
	private static String getPath(String expectedFileName) {
		return "versions/" + expectedFileName;
	}

	@ParameterizedTest
	@CsvSource({
			"v1.2.1.metadata.json, v1.2.1.fields.json",
			"v1.3.0.metadata.json, v1.3.0.fields.json",
	})
	void metadataToDatasetMetadataBlock(final String fileName, final String expectedFileName) throws IOException, DataverseMappingException, JSONException {
		final var metadata = jsonResourceReader.read(getPath(fileName), Metadata.class);

		final var datasetMetadataBlock = metadataBlockHelper.metadataToDatasetMetadataBlock(metadata);

		final var fields = datasetMetadataBlock.getRudi().getFields();
		val expectedJsonPath = getPath(expectedFileName);
		RudiAssertions.assertThat(fields).isJsonEqualToContentOf(expectedJsonPath);
	}

	@ParameterizedTest
	@CsvSource({
			"v1.2.1.fields.json, v1.2.1.metadata.json",
			"v1.3.0.fields.json, v1.3.0.metadata.json",
	})
	void datasetMetadataBlockToMetadata(final String fileName, final String expectedFileName) throws IOException, JSONException, DataverseMappingException {
		final var fields = jsonResourceReader.readList(getPath(fileName), DatasetMetadataBlockElementField.class);
		final var datasetMetadataBlock = new DatasetMetadataBlock()
				.rudi(new DatasetMetadataBlockElement()
						.fields(fields));

		final var metadata = metadataBlockHelper.datasetMetadataBlockToMetadata(datasetMetadataBlock, PERSISTENT_ID);

		val expectedJsonPath = getPath(expectedFileName);
		RudiAssertions.assertThat(metadata).isJsonEqualToContentOf(expectedJsonPath);
	}
}
