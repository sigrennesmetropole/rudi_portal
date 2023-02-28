package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper.DateTimeMapper;
import org.rudi.facet.kaccess.bean.Metadata;

import static org.rudi.common.test.RudiAssertions.assertThat;

public class MediaFieldsMapperTest {
	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final FieldGenerator fieldGenerator = new FieldGenerator();
	private final DateTimeMapper dateTimeMapper = new DateTimeMapper();
	private final MediaPrimitiveFieldsMapper mediaPrimitiveFieldsMapper = new MediaPrimitiveFieldsMapper(fieldGenerator, jsonResourceReader.getObjectMapper(), dateTimeMapper);
	private final MediaFieldsMapper mediaFieldsMapper = new MediaFieldsMapper(fieldGenerator, mediaPrimitiveFieldsMapper);

	@Test
	void metadataToFields_media() throws JSONException, IOException, DataverseMappingException {
		final Metadata metadata = jsonResourceReader.read("metadata/available_format/media_connector.metadata.json", Metadata.class);

		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();
		mediaFieldsMapper.metadataToFields(metadata, fields);

		assertThat(fields)
				.isJsonEqualToContentOf("metadata/available_format/media_connector.fields.json");
	}

	@Test
	void fieldsToMetadata_media() throws JSONException, IOException, DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = jsonResourceReader.readList("metadata/available_format/media_connector.fields.json", DatasetMetadataBlockElementField.class);

		final Metadata metadata = new Metadata();
		mediaFieldsMapper.fieldsToMetadata(new RootFields(fields), metadata);

		assertThat(metadata)
				.isJsonEqualToContentOf("metadata/available_format/media_connector.metadata.json");
	}
}
