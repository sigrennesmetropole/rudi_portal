package org.rudi.facet.dataverse.fields;

import org.jetbrains.annotations.Nullable;

class FieldSpecFromJavaFieldWithName extends FieldSpecFromJavaField {
	private final String fieldName;

	FieldSpecFromJavaFieldWithName(FieldSpec fieldSpec, String javaFieldName, String fieldName) {
		super(fieldSpec, javaFieldName);
		this.fieldName = fieldName;
	}

	FieldSpecFromJavaFieldWithName(FieldSpec fieldSpec, Class<?> javaFieldClass, String javaFieldName, String fieldName) {
		super(fieldSpec, javaFieldClass, javaFieldName);
		this.fieldName = fieldName;
	}

	@Override
	public @Nullable String getLocalName() {
		return fieldName;
	}
}
