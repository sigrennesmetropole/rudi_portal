package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import lombok.AllArgsConstructor;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

import java.util.Objects;

/**
 * Champ Dataverse de premier niveau
 */
@AllArgsConstructor
class RootField extends Field {
	final DatasetMetadataBlockElementField datasetMetadataBlockElementField;

	@Override
	public String getTypeName() {
		return datasetMetadataBlockElementField.getTypeName();
	}

	@Override
	public FieldTypeClass getTypeClass() {
		return datasetMetadataBlockElementField.getTypeClass();
	}

	@Override
	public Boolean getMultiple() {
		return datasetMetadataBlockElementField.getMultiple();
	}

	@Override
	public Object getValue() {
		return datasetMetadataBlockElementField.getValue();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		final RootField rootField = (RootField) o;
		return Objects.equals(datasetMetadataBlockElementField, rootField.datasetMetadataBlockElementField);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), datasetMetadataBlockElementField);
	}
}
