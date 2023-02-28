/**
 * 
 */
package org.rudi.facet.generator.pdf.exception;

/**
 * @author fni18300
 *
 */
public class ConvertorException extends Exception {

	private static final long serialVersionUID = -5643674161852298893L;

	public ConvertorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConvertorException(String message) {
		super(message);
	}

	public ConvertorException(Throwable cause) {
		super(cause);
	}

}
