package org.rudi.facet.dataverse.helper.query;

import lombok.RequiredArgsConstructor;
import org.rudi.facet.dataverse.fields.FieldSpec;

import java.util.Objects;

class RangedItemBuilder<T> extends ItemBuilder<RangedItemBuilder.RangedValue<T>> {

	public RangedItemBuilder(RangedValue<T> value) {
		super(value);
	}

	@Override
	protected boolean isDefaultValue(FieldSpec fieldSpec) {
		return value.isEqualToMinOrMax(fieldSpec.getDefaultValueIfMissing());
	}

	@Override
	protected String valueToString() {
		return "[" + valueToString(value.min) + " TO " + valueToString(value.max) + "]";
	}

	@RequiredArgsConstructor
	protected static class RangedValue<T> {
		final T min;
		final T max;

		<V> boolean isEqualToMinOrMax(V value) {
			return Objects.equals(value, min) || Objects.equals(value, max);
		}
	}

}
