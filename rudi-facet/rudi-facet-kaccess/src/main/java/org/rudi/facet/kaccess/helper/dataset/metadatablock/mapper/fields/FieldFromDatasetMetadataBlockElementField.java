package org.rudi.facet.kaccess.helper.dataset.metadatablock.mapper.fields;

import lombok.RequiredArgsConstructor;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.bean.FieldTypeClass;

@RequiredArgsConstructor
class FieldFromDatasetMetadataBlockElementField extends Field {

	private final DatasetMetadataBlockElementField blockElementField;

	@Override
	public String getTypeName() {
		return blockElementField.getTypeName();
	}

	@Override
	public FieldTypeClass getTypeClass() {
		return blockElementField.getTypeClass();
	}

	@Override
	public Boolean getMultiple() {
		return blockElementField.getMultiple();
	}

	@Override
	public Object getValue() {
		return blockElementField.getValue();
	}
}
