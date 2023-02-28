/**
 * 
 */
package org.rudi.facet.buckets3.exception;

/**
 * @author FNI18300
 *
 */
public class DocumentStorageException extends Exception {

	private static final long serialVersionUID = 6486623698725732490L;

	public DocumentStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public DocumentStorageException(String message) {
		super(message);
	}

	public DocumentStorageException(Throwable cause) {
		super(cause);
	}

}
