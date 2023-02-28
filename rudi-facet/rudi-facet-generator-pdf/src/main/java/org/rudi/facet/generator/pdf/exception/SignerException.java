/**
 * 
 */
package org.rudi.facet.generator.pdf.exception;

/**
 * @author fni18300
 *
 */
public class SignerException extends Exception {

	private static final long serialVersionUID = -5643674161852298893L;

	public SignerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SignerException(String message) {
		super(message);
	}

	public SignerException(Throwable cause) {
		super(cause);
	}

}
