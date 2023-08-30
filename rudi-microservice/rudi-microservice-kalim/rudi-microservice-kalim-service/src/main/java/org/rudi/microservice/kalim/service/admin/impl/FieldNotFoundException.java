package org.rudi.microservice.kalim.service.admin.impl;

import org.rudi.facet.dataverse.fields.FieldSpec;

/**
 * Champ manquant
 * 
 * @author FNI18300
 *
 */
class FieldNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -462913189094852358L;

	public FieldNotFoundException(FieldSpec fieldSpec) {
		super("Cannot find field : " + fieldSpec.getName());
	}
}
