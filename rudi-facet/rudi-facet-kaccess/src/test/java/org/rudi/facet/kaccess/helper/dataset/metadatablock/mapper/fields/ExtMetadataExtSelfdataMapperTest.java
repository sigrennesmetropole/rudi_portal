package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;

import static org.rudi.common.test.RudiAssertions.assertThat;

class ExtMetadataExtSelfdataMapperTest {
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final FieldGenerator fieldGenerator = new FieldGenerator();
	private final ExtMetadataExtSelfdataPrimitiveFieldsMapper extMetadataExtSelfdataPrimitiveFieldsMapper = new ExtMetadataExtSelfdataPrimitiveFieldsMapper(fieldGenerator, jsonResourceReader.getObjectMapper(), null);
	private final ExtMetadataExtSelfdataFieldsMapper extMetadataExtSelfdataFieldsMapper = new ExtMetadataExtSelfdataFieldsMapper(fieldGenerator, extMetadataExtSelfdataPrimitiveFieldsMapper);

	@ParameterizedTest
	@ValueSource(strings = {
			"matching_data_simple",
			"matching_data_with_validators",
			"all_fields"
	})
	void metadataToFields(final String filesBaseName) throws JSONException, IOException, DataverseMappingException {
		final Metadata metadata = jsonResourceReader.read(getMetadataPath(filesBaseName), Metadata.class);

		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();
		extMetadataExtSelfdataFieldsMapper.metadataToFields(metadata, fields);

		assertThat(fields)
				.isJsonEqualToContentOf(getFieldsPath(filesBaseName));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"matching_data_simple",
			"matching_data_with_validators",
			"all_fields"
	})
	void fieldsToMetadata(final String filesBaseName) throws JSONException, IOException, DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = jsonResourceReader.readList(getFieldsPath(filesBaseName), DatasetMetadataBlockElementField.class);

		final Metadata metadata = new Metadata();
		extMetadataExtSelfdataFieldsMapper.fieldsToMetadata(new RootFields(fields), metadata);

		assertThat(metadata)
				.isJsonEqualToContentOf(getMetadataPath(filesBaseName));
	}

	private static String getMetadataPath(final String fileBaseName) {
		return String.format("extMetadataFieldsMapper/%s.metadata.json", fileBaseName);
	}

	private static String getFieldsPath(final String fileBaseName) {
		return String.format("extMetadataFieldsMapper/%s.fields.json", fileBaseName);
	}
}
