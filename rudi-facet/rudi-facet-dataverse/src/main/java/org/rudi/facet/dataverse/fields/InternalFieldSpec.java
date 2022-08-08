package org.rudi.facet.dataverse.fields;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

class InternalFieldSpec extends ChildFieldSpec {

	private final String localName;

	InternalFieldSpec(@NotNull FieldSpec parent, String localName) {
		super(parent);
		this.localName = localName;
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	@Override
	public String getFacet() {
		return getParent().getFacet();
	}

	@Override
	protected Class<?> getType() {
		return null;
	}

	@Override
	public FieldTypeClass getTypeClass() {
		return null;
	}

	@Override
	public Class<?> getValueType() {
		return null;
	}

	@Override
	@Nullable String getDescription() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof InternalFieldSpec)) return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
