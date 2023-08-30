package org.rudi.facet.dataverse.api.search.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;
import org.rudi.facet.dataverse.fields.DatasetMetadataBlockElementSpec;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.FieldSpecNamingCase;
import org.rudi.facet.dataverse.fields.RootFieldSpec;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MetadatafieldsMapperUT {
	private final MetadatafieldsMapper metadatafieldsMapper = new MetadatafieldsMapper();

	private static final FieldSpec ROOT = new RootFieldSpec(Metadata.class, "root", FieldSpecNamingCase.SNAKE_CASE);
	private static final FieldSpec PRIMITIVE_FIELD = ROOT.newChildFromJavaField("primitiveField");
	private static final FieldSpec COUMPOUND_FIELD = ROOT.newChildFromJavaField("coumpoundField");
	private static final FieldSpec COUMPOUND_FIELD_ID = COUMPOUND_FIELD.newChildFromJavaField("id");
	private static final FieldSpec COUMPOUND_MULTIPLE_FIELD = ROOT.newChildFromJavaField("coumpoundMultipleField");
	private static final FieldSpec MULTIPLE_FIELD_ID = COUMPOUND_MULTIPLE_FIELD.newChildFromJavaField("id");
	private static final DatasetMetadataBlockElementSpec ELEMENT_SPEC = new DatasetMetadataBlockElementSpec(ROOT)
			.add(PRIMITIVE_FIELD)
			.add(COUMPOUND_FIELD,
					COUMPOUND_FIELD_ID)
			.add(COUMPOUND_MULTIPLE_FIELD,
					MULTIPLE_FIELD_ID);

	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	private static class Metadata {
		@JsonProperty("primitive_field")
		private UUID primitiveField;

		@JsonProperty("compound_field")
		private CompoundField coumpoundField;

		@JsonProperty("compound_multiple_field")
		private List<MultipleField> coumpoundMultipleField;
	}

	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	private static class CompoundField {
		private UUID id;
	}

	@SuppressWarnings({ "unused", "squid:S1068" }) // Classe utilisée pour l'introspection
	private static class MultipleField {
		private UUID id;
	}

	@Test
	void map_rootField() {
		final Set<String> metadatafields = metadatafieldsMapper.map(ELEMENT_SPEC, Collections.singletonList(ROOT));

		assertThat(metadatafields).containsExactly("root:*");
	}

	@Test
	void map_level1PrimitiveField() {
		final Set<String> metadatafields = metadatafieldsMapper.map(ELEMENT_SPEC, Collections.singletonList(PRIMITIVE_FIELD));

		assertThat(metadatafields).containsExactly("root:root_primitive_field");
	}

	@Test
	void map_level1CompoundField() {
		final Set<String> metadatafields = metadatafieldsMapper.map(ELEMENT_SPEC, Collections.singletonList(COUMPOUND_FIELD));

		assertThat(metadatafields).containsExactly("root:root_compound_field");
	}

	@Test
	void map_level2Field() {
		final Set<String> metadatafields = metadatafieldsMapper.map(ELEMENT_SPEC, Collections.singletonList(COUMPOUND_FIELD_ID));

		assertThat(metadatafields)
				.as("On ne peut pas demander seulement un champ fils d'un champ parent compound => on demande le champ parent")
				.containsExactly("root:root_compound_field");
	}

	@Test
	void map_level2FieldWithMultipleParent() {
		final Set<String> metadatafields = metadatafieldsMapper.map(ELEMENT_SPEC, Collections.singletonList(MULTIPLE_FIELD_ID));

		assertThat(metadatafields)
				.as("On ne peut pas demander seulement un champ fils d'un champ parent compound et multiple => on demande le champ parent")
				.containsExactly("root:root_compound_multiple_field");
	}
}