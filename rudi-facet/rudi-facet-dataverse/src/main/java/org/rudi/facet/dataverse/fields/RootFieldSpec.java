package org.rudi.facet.dataverse.fields;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

@Getter
public class RootFieldSpec extends FieldSpec {
	private final Class<?> type;
	private final String name;
	/**
	 * Mode de concaténation du nom du champ parent dans chaque champ fils
	 */
	private final FieldSpecNamingCase namingCase;

	public RootFieldSpec(Class<?> type, String name, FieldSpecNamingCase namingCase) {
		this.type = type;
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
	public Class<?> getValueType() {
		return type;
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
