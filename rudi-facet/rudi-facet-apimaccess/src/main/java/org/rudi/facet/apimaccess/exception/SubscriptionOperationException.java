package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.DevPortalSubscriptionSearchCriteria;
import org.wso2.carbon.apimgt.rest.api.devportal.Subscription;

public class SubscriptionOperationException extends APIManagerException {
	public SubscriptionOperationException(Throwable cause) {
		super(cause);
	}

	public SubscriptionOperationException(String id, String username, Throwable cause) {
		super(String.format("Operation failed on subscription with id = %s for username = %s", id, username), cause);
	}

	public SubscriptionOperationException(Subscription subscription, String username, Throwable cause) {
		super(String.format("Operation failed for username = %s on subscription : %s", username, subscription), cause);
	}

	public SubscriptionOperationException(DevPortalSubscriptionSearchCriteria criteria, String username, Throwable cause) {
		super(String.format("Operation failed for username = %s on criteria : %s", username, criteria), cause);
	}
}
