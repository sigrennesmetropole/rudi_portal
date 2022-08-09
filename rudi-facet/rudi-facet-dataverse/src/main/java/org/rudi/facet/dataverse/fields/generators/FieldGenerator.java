package org.rudi.facet.dataverse.fields.generators;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;
import org.rudi.facet.dataverse.utils.MessageUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Component
public class FieldGenerator {

	/**
	 * @return <code>null</code> si la valeur est null ou une chaîne vide, ou une liste/map vide (cf {@link #isEmpty(Object)}).
	 */
	@Nullable
	public DatasetMetadataBlockElementField generateField(FieldSpec spec, Object value) {
		final boolean multiple = spec.isMultiple();
		checkArgs(multiple, value);
		if (isEmpty(value)) {
			if (spec.isRequired()) {
				throw new NullPointerException(MessageUtils.buildErrorMessageRequiredMandatoryAttributes(spec));
			} else {
				return null;
			}
		}
		return new DatasetMetadataBlockElementField()
				.typeName(spec.getName())
				.typeClass(spec.getTypeClass())
				.multiple(multiple)
				.value(value);
	}

	private static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if (value instanceof Map) {
			return ((Map<?, ?>) value).isEmpty();
		}
		if (value instanceof List) {
			return ((List<?>) value).isEmpty();
		}
		if (value instanceof CharSequence) {
			return StringUtils.isEmpty((CharSequence) value);
		}
		return false;
	}

	private void checkArgs(boolean isMultiple, Object value) {
		if (!isMultiple && value instanceof List) {
			final List<?> values = (List<?>) value;
			if (values.size() > 1) {
				throw new IllegalArgumentException(
						String.format("Field is not multiple but %d arguments were supplied", values.size()));
			}
		}
		if (isMultiple && value != null && !(value instanceof List)) {
			throw new IllegalArgumentException(String.format("Field is multiple but value is instance of %s instead of List", value.getClass()));
		}
	}

}
