package org.rudi.facet.kaccess.helper.search.mapper.query;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rudi.facet.kaccess.constant.RudiMetadataField.RESTRICTED_ACCESS;

class FilterQueryTest {

	private static final boolean DEFAULT_VALUE_WHEN_RESTRICTED_ACCESS_IS_MISSING = false;

	/**
	 * RUDI-928
	 */
	@Test
	void addNullableFieldWithNotDefaultValue() {
		final FilterQuery filterQuery = new FilterQuery();
		filterQuery.add(RESTRICTED_ACCESS, !DEFAULT_VALUE_WHEN_RESTRICTED_ACCESS_IS_MISSING);
		assertThat(filterQuery).containsExactly("rudi_access_condition_confidentiality_restricted_access:true");
	}

	/**
	 * RUDI-928
	 */
	@Test
	void addNullableFieldWithDefaultValue() {
		final FilterQuery filterQuery = new FilterQuery();
		filterQuery.add(RESTRICTED_ACCESS, DEFAULT_VALUE_WHEN_RESTRICTED_ACCESS_IS_MISSING);
		assertThat(filterQuery).containsExactly("rudi_access_condition_confidentiality_restricted_access:false OR (*:* NOT rudi_access_condition_confidentiality_restricted_access:*)");
	}
}
