/**
 * 
 */
package org.rudi.facet.bpmn.exception;

/**
 * @author FNI18300
 *
 */
public class FormDefinitionException extends Exception {

	private static final long serialVersionUID = -6313510009433868085L;

	/**
	 * 
	 */
	public FormDefinitionException() {
	}

	/**
	 * @param message
	 */
	public FormDefinitionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FormDefinitionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FormDefinitionException(String message, Throwable cause) {
		super(message, cause);
	}

}
