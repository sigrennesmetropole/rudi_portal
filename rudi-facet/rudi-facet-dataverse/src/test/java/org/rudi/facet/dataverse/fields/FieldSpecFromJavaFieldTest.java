package org.rudi.facet.dataverse.fields;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.validation.constraints.NotNull;
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

	@ParameterizedTest(name = "Field {0} is required ? {1}")
	@CsvSource({
			"nonRequiredField, false",
			"requiredFieldThroughSchemaAnnotation, true",
			"requiredFieldThroughNotNullAnnotation, true",
	})
	void isRequired_notAnnotated(final String javaFieldName, final boolean expectedRequiredValue) {
		final var field = ROOT.newChildFromJavaField(javaFieldName);
		assertThat(field).hasFieldOrPropertyWithValue("required", expectedRequiredValue);
	}

	@SuppressWarnings("unused") // used to generate FieldSpec
	@Getter
	@Setter
	private static class SampleObject {
		@JsonProperty("string_field")
		private String stringField;

		@JsonProperty("list_field")
		private List<String> listField;

		private String nonRequiredField;

		private String requiredFieldThroughSchemaAnnotation;

		@Schema(required = true)
		public String getRequiredFieldThroughSchemaAnnotation() {
			return requiredFieldThroughSchemaAnnotation;
		}

		private String requiredFieldThroughNotNullAnnotation;

		@NotNull
		public String getRequiredFieldThroughNotNullAnnotation() {
			return requiredFieldThroughNotNullAnnotation;
		}
	}
}
