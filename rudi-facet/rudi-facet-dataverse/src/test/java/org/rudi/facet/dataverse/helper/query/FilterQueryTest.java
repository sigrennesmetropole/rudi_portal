package org.rudi.facet.dataverse.helper.query;

import org.junit.jupiter.api.Test;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.fields.FieldSpecNamingCase;
import org.rudi.facet.dataverse.fields.RootFieldSpec;

import static org.assertj.core.api.Assertions.assertThat;

class FilterQueryTest {

	private static final FieldSpec ROOT = new RootFieldSpec(SampleObject.class, "root", FieldSpecNamingCase.SNAKE_CASE);
	private static final boolean DEFAULT_VALUE_IF_MISSING = false;
	private static final FieldSpec FIELD = ROOT.newChildFromJavaField("field")
			.defaultValueIfMissing(DEFAULT_VALUE_IF_MISSING);

	/**
	 * RUDI-928
	 */
	@Test
	void addNullableFieldWithNotDefaultValue() {
		final FilterQuery filterQuery = new FilterQuery();
		filterQuery.add(FIELD, !DEFAULT_VALUE_IF_MISSING);
		assertThat(filterQuery).containsExactly("root_field:true");
	}

	/**
	 * RUDI-928
	 */
	@Test
	void addNullableFieldWithDefaultValue() {
		final FilterQuery filterQuery = new FilterQuery();
		filterQuery.add(FIELD, DEFAULT_VALUE_IF_MISSING);
		assertThat(filterQuery).containsExactly("root_field:false OR (*:* NOT root_field:*)");
	}

	private static class SampleObject {
		private boolean field;
	}
}
