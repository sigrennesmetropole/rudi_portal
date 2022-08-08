package org.rudi.facet.dataverse.fields;

import org.jetbrains.annotations.NotNull;

public class SortableCopyField extends InternalFieldSpec {
	private SortableCopyField(@NotNull FieldSpec source) {
		super(source, "sortable");
	}

	public static SortableCopyField from(FieldSpec fieldSpec) {
		return new SortableCopyField(fieldSpec);
	}
}
