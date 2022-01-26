package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import lombok.AllArgsConstructor;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

import java.util.Map;
import java.util.Objects;

/**
 * Champ Dataverse qui n'est pas au niveau 1 (sinon c'est un {@link RootField}). Construit à partir d'une map JSON.
 */
@AllArgsConstructor
class ChildField extends Field {
	final Map<?, ?> jsonField;

	private String getString(final String property) {
		final Object jsonValue = jsonField.get(property);
		return jsonValue == null ? null : jsonValue.toString();
	}

	@Override
	public String getTypeName() {
		return getString("typeName");
	}

	@Override
	public FieldTypeClass getTypeClass() {
		final String jsonValue = getString("typeClass");
		return jsonValue == null ? null : FieldTypeClass.fromValue(jsonValue);
	}

	@Override
	public Boolean getMultiple() {
		final String jsonValue = getString("multiple");
		return jsonValue == null ? null : Boolean.valueOf(jsonValue);
	}

	@Override
	public Object getValue() {
		return jsonField.get("value");
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		final ChildField childField1 = (ChildField) o;
		return Objects.equals(jsonField, childField1.jsonField);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), jsonField);
	}
}
