package org.rudi.wso2.mediation;

class CannotLoadPropertyException extends RuntimeException {
	private static final long serialVersionUID = 3059663975469583122L;

	public CannotLoadPropertyException(String propertyName, Throwable cause) {
		super(String.format("Cannot load property %s", propertyName), cause);
	}
}
