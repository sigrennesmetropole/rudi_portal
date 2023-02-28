package org.rudi.microservice.kalim.service.admin.impl;

import java.util.UUID;

import org.rudi.facet.dataverse.fields.FieldSpec;

class MissingDatasetPropertyException extends IllegalArgumentException {
	private static final long serialVersionUID = -7050512372501393874L;

	MissingDatasetPropertyException(final UUID metadataGlobalId, final FieldSpec fieldSpec) {
		super(String.format("Dataset %s is missing property %s.", metadataGlobalId, fieldSpec));
	}
}
