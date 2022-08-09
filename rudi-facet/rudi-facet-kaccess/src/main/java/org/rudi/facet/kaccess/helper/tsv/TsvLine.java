package org.rudi.facet.kaccess.helper.tsv;

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

@Builder
class TsvLine {
	final String firstColumn = StringUtils.EMPTY;
	final String name;
	final String title;
	final String description;
	final String watermark = StringUtils.EMPTY;
	final String fieldType;
	final Integer displayOrder;
	final String displayFormat;
	final Boolean advancedSearchField;
	final Boolean allowControlledVocabulary;
	final Boolean allowmultiples;
	final Boolean facetable;
	final Boolean displayoncreate;
	final Boolean required;
	final String parent;
	final String metadatablock_id;
	final String termURI;

	public String toString() {
		return StringUtils.joinWith("\t",
				firstColumn,
				name,
				title,
				description,
				watermark,
				fieldType,
				displayOrder,
				displayFormat,
				format(advancedSearchField),
				format(allowControlledVocabulary),
				format(allowmultiples),
				format(facetable),
				format(displayoncreate),
				format(required),
				parent,
				metadatablock_id,
				termURI);
	}

	private String format(Boolean value) {
		if (value == null) {
			return null;
		} else {
			return value.toString().toUpperCase();
		}
	}
}
