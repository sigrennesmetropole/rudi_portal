package org.rudi.common.facade.config.filter;

/**
 * @author FNI18300
 */
public class RefreshTokenExpiredException extends Exception {

	private static final long serialVersionUID = 586242607016349085L;

	public RefreshTokenExpiredException(String message) {
		super(message);
	}

	public RefreshTokenExpiredException(String message, Throwable cause) {
		super(message, cause);
	}
}
