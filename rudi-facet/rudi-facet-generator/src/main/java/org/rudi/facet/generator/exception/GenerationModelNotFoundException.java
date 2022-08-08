/**
 * 
 */
package org.rudi.facet.generator.exception;

/**
 * @author fni18300
 *
 */
public class GenerationModelNotFoundException extends Exception {

	private static final long serialVersionUID = -5643674161852298893L;

	public GenerationModelNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerationModelNotFoundException(String message) {
		super(message);
	}

	public GenerationModelNotFoundException(Throwable cause) {
		super(cause);
	}

}
