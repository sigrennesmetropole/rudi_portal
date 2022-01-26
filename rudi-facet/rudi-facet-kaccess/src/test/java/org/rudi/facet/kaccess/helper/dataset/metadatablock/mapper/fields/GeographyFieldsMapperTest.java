package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.rudi.common.test.RudiAssertions.assertThat;

class GeographyFieldsMapperTest {

	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final FieldGenerator fieldGenerator = new FieldGenerator();
	private final GeographyPrimitiveFieldsMapper geographyPrimitiveFieldsMapper = new GeographyPrimitiveFieldsMapper(fieldGenerator, jsonResourceReader.getObjectMapper());
	private final GeographyFieldsMapper geographyFieldsMapper = new GeographyFieldsMapper(fieldGenerator, geographyPrimitiveFieldsMapper);

	@ParameterizedTest
	@ValueSource(strings = {
			"geography",
			"geography-minimum",
			"geography-empty-properties",
			"geography-non-empty-properties"
	})
	void metadataToFields(final String filesBaseName) throws IOException, JSONException, DataverseMappingException {
		final Metadata metadata = jsonResourceReader.read(getMetadataPath(filesBaseName), Metadata.class);

		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();
		geographyFieldsMapper.metadataToFields(metadata, fields);

		assertThat(fields)
				.isJsonEqualToContentOf(getFieldsPath(filesBaseName));
	}

	private static String getMetadataPath(final String fileBaseName) {
		return String.format("geographyFieldsMapper/%s.metadata.json", fileBaseName);
	}

	private static String getFieldsPath(final String fileBaseName) {
		return String.format("geographyFieldsMapper/%s.fields.json", fileBaseName);
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"geography",
			"geography-minimum",
			"geography-non-empty-properties"
	})
	void fieldsToMetadata(final String filesBaseName) throws IOException, JSONException, DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = jsonResourceReader.readList(getFieldsPath(filesBaseName), DatasetMetadataBlockElementField.class);

		final Metadata metadata = new Metadata();
		geographyFieldsMapper.fieldsToMetadata(new RootFields(fields), metadata);

		assertThat(metadata)
				.isJsonEqualToContentOf(getMetadataPath(filesBaseName));
	}

	@Test
	void metadataToFields_unknownGeoJsonType() {
		assertThatThrownBy(() -> jsonResourceReader.read(getMetadataPath("geography-unknown-geojsontype"), Metadata.class))
				.isInstanceOf(InvalidTypeIdException.class)
				.hasMessageContaining("Could not resolve type id 'Pointu'");
	}

	@Test
	void metadataToFields_GeoJsonAsString() {
		assertThatThrownBy(() -> jsonResourceReader.read(getMetadataPath("geography-geojsonasstring"), Metadata.class))
				.isInstanceOf(InvalidTypeIdException.class)
				.hasMessageContaining("Missing type id");
	}
}
