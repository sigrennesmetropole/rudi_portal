package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.generators.FieldGenerator;
import org.rudi.facet.kaccess.bean.Metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.rudi.common.test.RudiAssertions.assertThat;

class AccessConditionFieldsMapperTest {

	private final JsonResourceReader jsonResourceReader = new JsonResourceReader();
	private final FieldGenerator fieldGenerator = new FieldGenerator();
	private final AccessConditionPrimiviteFieldsMapper accessConditionPrimiviteFieldsMapper = new AccessConditionPrimiviteFieldsMapper(fieldGenerator, jsonResourceReader.getObjectMapper());
	private final AccessConditionCompoundFieldsMapper accessConditionCompoundFieldsMapper = new AccessConditionCompoundFieldsMapper(fieldGenerator);
	private final AccessConditionFieldsMapper accessConditionFieldsMapper = new AccessConditionFieldsMapper(fieldGenerator, accessConditionPrimiviteFieldsMapper, accessConditionCompoundFieldsMapper);

	@Test
	void metadataToFields_standard_licence() throws JSONException, IOException, DataverseMappingException {
		final Metadata metadata = jsonResourceReader.read("accessConditionFieldsMapper/standard_licence.metadata.json", Metadata.class);

		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();
		accessConditionFieldsMapper.metadataToFields(metadata, fields);

		assertThat(fields)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/standard_licence.fields.json");
	}

	@Test
	void metadataToFields_custom_licence() throws JSONException, IOException, DataverseMappingException {
		final Metadata metadata = jsonResourceReader.read("accessConditionFieldsMapper/custom_licence.metadata.json", Metadata.class);

		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();
		accessConditionFieldsMapper.metadataToFields(metadata, fields);

		assertThat(fields)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/custom_licence.fields.json");
	}

	@Test
	void metadataToFields_others() throws JSONException, IOException, DataverseMappingException {
		final Metadata metadata = jsonResourceReader.read("accessConditionFieldsMapper/others.metadata.json", Metadata.class);

		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();
		accessConditionFieldsMapper.metadataToFields(metadata, fields);

		assertThat(fields)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/others.fields.json");
	}

	@Test
	void metadataToFields_empty() throws JSONException, IOException, DataverseMappingException {
		final Metadata metadata = jsonResourceReader.read("accessConditionFieldsMapper/empty.metadata.in.json", Metadata.class);

		final List<DatasetMetadataBlockElementField> fields = new ArrayList<>();
		accessConditionFieldsMapper.metadataToFields(metadata, fields);

		assertThat(fields)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/empty.fields.json");
	}

	@Test
	void fieldsToMetadata_standard_licence() throws JSONException, IOException, DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = jsonResourceReader.readList("accessConditionFieldsMapper/standard_licence.fields.json", DatasetMetadataBlockElementField.class);

		final Metadata metadata = new Metadata();
		accessConditionFieldsMapper.fieldsToMetadata(new RootFields(fields), metadata);

		assertThat(metadata)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/standard_licence.metadata.json");
	}

	@Test
	void fieldsToMetadata_custom_licence() throws JSONException, IOException, DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = jsonResourceReader.readList("accessConditionFieldsMapper/custom_licence.fields.json", DatasetMetadataBlockElementField.class);

		final Metadata metadata = new Metadata();
		accessConditionFieldsMapper.fieldsToMetadata(new RootFields(fields), metadata);

		assertThat(metadata)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/custom_licence.metadata.json");
	}

	@Test
	void fieldsToMetadata_others() throws JSONException, IOException, DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = jsonResourceReader.readList("accessConditionFieldsMapper/others.fields.json", DatasetMetadataBlockElementField.class);

		final Metadata metadata = new Metadata();
		accessConditionFieldsMapper.fieldsToMetadata(new RootFields(fields), metadata);

		assertThat(metadata)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/others.metadata.json");
	}

	@Test
	void fieldsToMetadata_empty() throws JSONException, IOException, DataverseMappingException {
		final List<DatasetMetadataBlockElementField> fields = jsonResourceReader.readList("accessConditionFieldsMapper/empty.fields.json", DatasetMetadataBlockElementField.class);

		final Metadata metadata = new Metadata();
		accessConditionFieldsMapper.fieldsToMetadata(new RootFields(fields), metadata);

		assertThat(metadata)
				.isJsonEqualToContentOf("accessConditionFieldsMapper/empty.metadata.out.json");
	}
}