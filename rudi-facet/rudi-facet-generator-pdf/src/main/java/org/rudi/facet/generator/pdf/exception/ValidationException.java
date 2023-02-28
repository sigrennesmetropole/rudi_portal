/**
 * 
 */
package org.rudi.facet.generator.pdf.exception;

/**
 * @author fni18300
 *
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = -5643674161852298893L;

	public ValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(Throwable cause) {
		super(cause);
	}

}
