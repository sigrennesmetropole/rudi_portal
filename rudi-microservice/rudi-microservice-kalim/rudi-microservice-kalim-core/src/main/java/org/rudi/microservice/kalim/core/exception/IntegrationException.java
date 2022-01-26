/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.core.exception;

/**
 * @author FNI18300
 *
 */
public class IntegrationException extends Exception {

	private static final long serialVersionUID = 1866500804271933081L;

	/**
	 * 
	 */
	public IntegrationException() {
	}

	/**
	 * @param message
	 */
	public IntegrationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public IntegrationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public IntegrationException(String message, Throwable cause) {
		super(message, cause);
	}

}
