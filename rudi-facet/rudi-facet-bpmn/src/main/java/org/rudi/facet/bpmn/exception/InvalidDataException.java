package org.rudi.facet.bpmn.exception;

/**
 * Exception thrown when an error occured because of invalid data send by user.
 */
public class InvalidDataException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidDataException() {
		super();
	}

	public InvalidDataException(final String message) {
		super(message);
	}

	public InvalidDataException(final String message, Exception e) {
		super(message, e);
	}
}
