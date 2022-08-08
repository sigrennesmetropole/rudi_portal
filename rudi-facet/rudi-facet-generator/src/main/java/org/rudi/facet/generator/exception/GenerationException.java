/**
 * 
 */
package org.rudi.facet.generator.exception;

/**
 * @author fni18300
 *
 */
public class GenerationException extends Exception {

	private static final long serialVersionUID = -5643674161852298893L;

	public GenerationException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerationException(String message) {
		super(message);
	}

	public GenerationException(Throwable cause) {
		super(cause);
	}

}
