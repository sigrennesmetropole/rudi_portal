package org.rudi.wso2.mediation;

class CannotCreatePropertiesException extends RuntimeException {
	private static final long serialVersionUID = -7938521643583675090L;

	public CannotCreatePropertiesException(ReflectiveOperationException cause) {
		super(cause);
	}
}
