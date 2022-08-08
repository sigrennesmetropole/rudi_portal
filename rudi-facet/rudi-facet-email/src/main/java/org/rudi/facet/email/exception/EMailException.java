/**
 * 
 */
package org.rudi.facet.email.exception;

/**
 * Exception liée à l'envoie de courriel
 * 
 * @author fni18300
 *
 */
public class EMailException extends Exception {

	private static final long serialVersionUID = -3654159262937773199L;

	public EMailException(String message, Throwable cause) {
		super(message, cause);
	}

	public EMailException(String message) {
		super(message);
	}

}
