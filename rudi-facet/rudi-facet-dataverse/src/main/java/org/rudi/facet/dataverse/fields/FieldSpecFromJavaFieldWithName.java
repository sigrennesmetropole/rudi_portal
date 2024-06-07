package org.rudi.facet.dataverse.fields;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

class FieldSpecFromJavaFieldWithName extends FieldSpecFromJavaField {
	private final String fieldName;

	FieldSpecFromJavaFieldWithName(FieldSpec fieldSpec, String javaFieldName, String fieldName) {
		super(fieldSpec, javaFieldName);
		this.fieldName = fieldName;
	}

	FieldSpecFromJavaFieldWithName(FieldSpec fieldSpec, Class<?> javaFieldClass, String javaFieldName,
			String fieldName) {
		super(fieldSpec, javaFieldClass, javaFieldName);
		this.fieldName = fieldName;
	}

	@Override
	public @Nullable String getLocalName() {
		return fieldName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(fieldName);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof FieldSpecFromJavaFieldWithName)) {
			return false;
		}
		FieldSpecFromJavaFieldWithName other = (FieldSpecFromJavaFieldWithName) obj;
		return Objects.equals(fieldName, other.fieldName);
	}
}
