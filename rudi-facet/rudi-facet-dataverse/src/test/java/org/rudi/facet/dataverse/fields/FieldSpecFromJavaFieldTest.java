package org.rudi.facet.dataverse.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FieldSpecFromJavaFieldTest {

	private static final FieldSpec ROOT = new RootFieldSpec(SampleObject.class, "root", FieldSpecNamingCase.SNAKE_CASE);

	@Test
	void getLocalName() {
		final var field = ROOT.newChildFromJavaField("stringField");
		assertThat(field.getLocalName()).isEqualTo("string_field");
	}

	@Test
	void getName() {
		final var field = ROOT.newChildFromJavaField("stringField");
		assertThat(field.getName()).isEqualTo("root_string_field");
	}

	@Test
	void getFacet() {
		final var field = ROOT.newChildFromJavaField("stringField");
		assertThat(field.getFacet()).isEqualTo("string_field");
	}

	@Test
	void getFacet_index() {
		final var field = ROOT.newChildFromJavaField("stringField")
				.getIndex();
		assertThat(field.getFacet()).isEqualTo("string_field");
	}

	@Test
	void getIndex() {
		final var field = ROOT.newChildFromJavaField("stringField");
		assertThat(field.getIndex().getName()).isEqualTo("root_string_field_s");
	}

	@Test
	void getIndex_multiple() {
		final var field = ROOT.newChildFromJavaField("listField");
		assertThat(field.getIndex().getName()).isEqualTo("root_list_field_ss");
	}

	@Test
	void getSortableField_isSortable() {
		final var field = ROOT.newChildFromJavaField("stringField")
				.isSortable(true);
		assertThat(field.getSortableField().getName()).isEqualTo("root_string_field");
	}

	@Test
	void getSortableField_isNotSortable() {
		final var field = ROOT.newChildFromJavaField("stringField")
				.isSortable(false);
		assertThat(field.getSortableField().getName()).isEqualTo("root_string_field_s");
	}

	private static class SampleObject {
		@SuppressWarnings("unused") // used to generate FieldSpec
		@JsonProperty("string_field")
		private String stringField;

		@SuppressWarnings("unused") // used to generate FieldSpec
		@JsonProperty("list_field")
		private List<String> listField;
	}
}
