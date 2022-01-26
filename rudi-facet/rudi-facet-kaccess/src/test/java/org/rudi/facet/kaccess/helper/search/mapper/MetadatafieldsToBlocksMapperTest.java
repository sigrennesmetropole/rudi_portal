package org.rudi.facet.kaccess.helper.search.mapper;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlock;
import org.rudi.facet.dataverse.fields.exceptions.MismatchedChildrenValuesCount;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.constant.RudiMetadataField;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.rudi.common.test.RudiAssertions.assertThat;


class MetadatafieldsToBlocksMapperTest {

	private final MetadatafieldsToBlocksMapper metadatafieldsToBlocksMapper = new MetadatafieldsToBlocksMapper(new FieldGenerator());
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();

	@ParameterizedTest
	@ValueSource(strings = { "custom_licence", "empty", "others", "standard_licence" })
	void map_accessCondition(final String fileGroup) throws IOException, JSONException {
		final Map<String, Object> metadatafields = readMetadatafields(fileGroup);

		final DatasetMetadataBlock block = metadatafieldsToBlocksMapper.map(metadatafields);

		final String expectedJsonPath = getJsonPath("accessConditionFieldsMapper/%s.fields.json", fileGroup);
		assertThat(block.getRudi().getFields()).isJsonEqualToContentOf(expectedJsonPath);
	}

	@ParameterizedTest
	@ValueSource(strings = { "jdd-ouvert" })
	void map_citation(final String fileGroup) throws IOException, JSONException {
		final Map<String, Object> metadatafields = readMetadatafields(fileGroup);

		final DatasetMetadataBlock block = metadatafieldsToBlocksMapper.map(metadatafields);

		final String expectedJsonPath = getJsonPath("datasetMetadataBlock/citation/%s.json", fileGroup);
		assertThat(block.getCitation()).isJsonEqualToContentOf(expectedJsonPath);
	}

	@ParameterizedTest
	@ValueSource(strings = { "jdd-ouvert", "medias-count-equals" })
	void map_block(final String fileGroup) throws IOException, JSONException {
		final Map<String, Object> metadatafields = readMetadatafields(fileGroup);

		final DatasetMetadataBlock block = metadatafieldsToBlocksMapper.map(metadatafields);

		final String expectedJsonPath = getJsonPath("datasetMetadataBlock/%s.json", fileGroup);
		assertThat(block).isJsonEqualToContentOf(expectedJsonPath);
	}

	@Test
	void map_block_countNotEquals() throws IOException {
		final Map<String, Object> metadatafields = readMetadatafields("medias-count-not-equals");

		assertThatThrownBy(() -> metadatafieldsToBlocksMapper.map(metadatafields))
				.isInstanceOf(MismatchedChildrenValuesCount.class)
				.hasMessageContaining(RudiMetadataField.MEDIA.getName());
	}

	private Map<String, Object> readMetadatafields(final String fileGroup) throws IOException {
		final String inputJsonPath = getJsonPath("metadatafields/%s.json", fileGroup);
		return jsonResourceReader.readMap(inputJsonPath);
	}

	private static String getJsonPath(String pathFormat, String fileGroup) {
		return String.format(pathFormat, fileGroup);
	}

}
