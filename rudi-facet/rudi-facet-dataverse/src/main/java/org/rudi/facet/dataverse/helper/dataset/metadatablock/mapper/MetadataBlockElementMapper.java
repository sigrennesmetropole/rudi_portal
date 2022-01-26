package org.rudi.facet.dataverse.helper.dataset.metadatablock.mapper;

import org.rudi.facet.dataverse.api.exceptions.DataverseMappingException;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElement;
import org.rudi.facet.dataverse.bean.DatasetMetadataBlockElementField;
import org.rudi.facet.dataverse.fields.FieldSpec;

import java.util.List;
import java.util.Map;

public interface MetadataBlockElementMapper<T> {

	default DatasetMetadataBlockElement dataToDatasetMetadataBlockElement(T data) throws DataverseMappingException {
		return new DatasetMetadataBlockElement().fields(createFields(data)).displayName(getDisplayName());
	}

	void datasetMetadataBlockElementToData(DatasetMetadataBlockElement datasetMetadataBlockElement, T data) throws DataverseMappingException;

	String getDisplayName();

	List<DatasetMetadataBlockElementField> createFields(T data) throws DataverseMappingException;

	void addOptionalPrimitiveField(Object value, Map<String, Object> map, FieldSpec fieldSpec);

}
