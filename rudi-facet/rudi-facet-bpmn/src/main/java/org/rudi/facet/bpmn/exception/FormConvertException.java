/**
 * 
 */
package org.rudi.facet.bpmn.exception;

/**
 * @author FNI18300
 *
 */
public class FormConvertException extends Exception {

	private static final long serialVersionUID = -6313510009433868085L;

	/**
	 * 
	 */
	public FormConvertException() {
	}

	/**
	 * @param message
	 */
	public FormConvertException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FormConvertException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FormConvertException(String message, Throwable cause) {
		super(message, cause);
	}

}
