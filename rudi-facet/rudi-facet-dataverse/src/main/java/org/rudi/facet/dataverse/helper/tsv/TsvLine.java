package org.rudi.facet.dataverse.helper.tsv;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

abstract class TsvLine {
	static final String COLUMN_SEPARATOR = "\t";
	final String firstColumn = StringUtils.EMPTY;

	public abstract String toString();

	protected String format(Boolean value) {
		if (value == null) {
			return null;
		} else {
			return value.toString().toUpperCase();
		}
	}

	protected abstract String getHashString();

	@Override
	public int hashCode() {
		return getHashString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TsvLine)) return false;
		if (!this.getClass().isInstance(o)) return false;
		final TsvLine that = (TsvLine) o;
		return Objects.equals(getHashString(), that.getHashString());
	}
}
