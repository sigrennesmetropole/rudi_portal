/**
 * RUDI Portail
 */
package org.rudi.facet.cms.exception;

/**
 * @author FNI18300
 *
 */
public class CmsException extends Exception {

	private static final long serialVersionUID = 1L;

	public CmsException() {
		super();
	}

	public CmsException(final String message) {
		super(message);
	}

	public CmsException(final String message, final Throwable exception) {
		super(message, exception);
	}

	public CmsException(Throwable e) {
		super(e);
	}

}
