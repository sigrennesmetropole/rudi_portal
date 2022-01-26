package org.rudi.facet.dataverse.fields.exceptions;

import org.rudi.facet.dataverse.fields.FieldSpec;

public class MismatchedChildrenValuesCount extends RuntimeException {
	public MismatchedChildrenValuesCount(FieldSpec parentFieldSpec, Integer count1, Integer count2) {
		super(
				String.format("Every children of %s must have the same amount of values in metadatafields. Found at least one difference : %s != %s.",
						parentFieldSpec.getName(),
						count1,
						count2
				));
	}
}
