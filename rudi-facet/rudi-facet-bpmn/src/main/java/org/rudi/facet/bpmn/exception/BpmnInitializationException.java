/**
 * 
 */
package org.rudi.facet.bpmn.exception;

;

/**
 * @author FNI18300
 *
 */
public class BpmnInitializationException extends Exception {

	private static final long serialVersionUID = -6313510009433868085L;

	/**
	 * 
	 */
	public BpmnInitializationException() {
	}

	/**
	 * @param message
	 */
	public BpmnInitializationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public BpmnInitializationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public BpmnInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

}
