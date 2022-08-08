package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;

public class UpdateAPILifecycleStatusException extends APIManagerException {
	public UpdateAPILifecycleStatusException(String apiId, APILifecycleStatusAction action, Throwable e) {
		super(message(apiId, action), e);
	}

	private static String message(String apiId, APILifecycleStatusAction action) {
		return String.format("Failed to %s API with id = %s", action.getValue(), apiId);
	}
}
