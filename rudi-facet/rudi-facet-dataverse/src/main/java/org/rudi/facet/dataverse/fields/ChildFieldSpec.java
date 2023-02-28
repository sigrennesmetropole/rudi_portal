package org.rudi.facet.dataverse.fields;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

import lombok.Getter;

public abstract class ChildFieldSpec extends FieldSpec {

	private static final String ID_SEPARATOR = "_";

	@Nonnull
	@Getter
	private final FieldSpec parent;

	ChildFieldSpec(@Nonnull FieldSpec parent) {
		this.parent = parent;
	}

	/**
	 * @return le nom du champ JsonProperty
	 */
	public abstract String getLocalName();

	@Override
	public FieldTypeClass getTypeClass() {
		if (allowControlledVocabulary()) {
			return FieldTypeClass.CONTROLLEDVOCABULARY;
		} else if (CollectionUtils.isNotEmpty(getDirectChildren())) {
			return FieldTypeClass.COMPOUND;
		}
		return FieldTypeClass.PRIMITIVE;
	}

	@Override
	public String getName() {
		return concatenateLocalNameAfter(parent.getName());
	}

	@Override
	public String getFacet() {
		return concatenateLocalNameAfter(parent.getFacet());
	}

	private String concatenateLocalNameAfter(String parentName) {
		if (StringUtils.isEmpty(parentName)) {
			return getLocalName();
		}
		if (getNamingCase() == FieldSpecNamingCase.SNAKE_CASE) {
			return parentName + ID_SEPARATOR + getLocalName();
		} else {
			return parentName + StringUtils.capitalize(getLocalName());
		}
	}

	@Override
	public FieldSpecNamingCase getNamingCase() {
		return parent.getNamingCase();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		return (obj instanceof ChildFieldSpec);
	}

}
