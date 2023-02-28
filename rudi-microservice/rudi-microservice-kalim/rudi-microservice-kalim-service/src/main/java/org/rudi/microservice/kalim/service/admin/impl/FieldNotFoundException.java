package org.rudi.microservice.kalim.service.admin.impl;

import org.rudi.facet.dataverse.fields.FieldSpec;

class FieldNotFoundException extends RuntimeException {
	public FieldNotFoundException(FieldSpec fieldSpec) {
		super("Cannot find field : " + fieldSpec.getName());
	}
}
