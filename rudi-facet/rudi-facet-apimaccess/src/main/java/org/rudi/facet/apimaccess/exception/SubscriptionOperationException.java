package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.ApplicationAPISubscription;
import org.rudi.facet.apimaccess.bean.ApplicationAPISubscriptionSearchCriteria;

public class SubscriptionOperationException extends APIManagerException {
	public SubscriptionOperationException(Throwable cause) {
		super(cause);
	}

	public SubscriptionOperationException(String id, String username, Throwable cause) {
		super(String.format("Operation failed on subscription with id = %s for username = %s", id, username), cause);
	}

	public SubscriptionOperationException(ApplicationAPISubscription subscription, String username, Throwable cause) {
		super(String.format("Operation failed for username = %s on subscription : %s", username, subscription), cause);
	}

	public SubscriptionOperationException(ApplicationAPISubscriptionSearchCriteria criteria, String username, Throwable cause) {
		super(String.format("Operation failed for username = %s on criteria : %s", username, criteria), cause);
	}
}
