package org.rudi.facet.apimaccess.exception;

public class APIManagerException extends Exception {

	private static final long serialVersionUID = 3495136618780496111L;

	public APIManagerException() {
		super();
	}

	public APIManagerException(final String message) {
		super(message);
	}

	public APIManagerException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public APIManagerException(Throwable cause) {
		super(cause);
	}
}
