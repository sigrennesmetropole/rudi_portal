package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import org.jetbrains.annotations.Nullable;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootFields {
	final List<DatasetMetadataBlockElementField> sourceFields;
	final Map<FieldSpec, Field> knownFields;

	public RootFields(List<DatasetMetadataBlockElementField> sourceFields) {
		this.sourceFields = sourceFields;
		knownFields = new HashMap<>(sourceFields.size());
	}

	@Nullable
	Field get(FieldSpec fieldSpec) {
		return knownFields.computeIfAbsent(fieldSpec, k -> {
			final DatasetMetadataBlockElementField sourceField = sourceFields.stream()
					.filter(currentSourceField -> currentSourceField.getTypeName().equals(fieldSpec.getName()))
					.findFirst()
					.orElse(null);
			return sourceField == null ? null : new RootField(sourceField);
		});
	}
}
