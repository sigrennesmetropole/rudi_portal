package org.rudi.facet.dataverse.fields;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

import lombok.Getter;

@Getter
public class RootFieldSpec extends FieldSpec {
	private final Class<?> javaType;
	private final String name;
	/**
	 * Mode de concaténation du nom du champ parent dans chaque champ fils
	 */
	private final FieldSpecNamingCase namingCase;

	public RootFieldSpec(Class<?> javaType, String name, FieldSpecNamingCase namingCase) {
		this.javaType = javaType;
		this.name = name;
		this.namingCase = namingCase;
	}

	@Override
	public String getLocalName() {
		return name;
	}

	@Override
	public String getFacet() {
		return StringUtils.EMPTY;
	}

	@Override
	public @Nullable String getDescription() {
		return null;
	}

	@Override
	public FieldTypeClass getTypeClass() {
		return FieldTypeClass.COMPOUND;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RootFieldSpec)) return false;
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
