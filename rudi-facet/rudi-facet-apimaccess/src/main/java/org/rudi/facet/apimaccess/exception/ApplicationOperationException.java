package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.Application;
import org.rudi.facet.apimaccess.bean.ApplicationSearchCriteria;

public class ApplicationOperationException extends APIManagerException {
	public ApplicationOperationException(ApplicationSearchCriteria criteria, String username, Throwable cause) {
		super(String.format("Operation failed for username = %s on criteria : %s", username, criteria), cause);
	}

	public ApplicationOperationException(String id, String username, Throwable cause) {
		super(String.format("Operation failed for username = %s on application with id = %s", username, id), cause);
	}

	public ApplicationOperationException(Application application, String username, Throwable cause) {
		super(String.format("Operation failed for username = %s on application : %s", username, application), cause);
	}
}
