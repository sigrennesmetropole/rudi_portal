package org.rudi.wso2.mediation;

class MissingRequiredPropertyException extends RuntimeException {
	private static final long serialVersionUID = -6486556300148874714L;

	public MissingRequiredPropertyException(String propertyName) {
		super(String.format("Missing property %s", propertyName));
	}
}
