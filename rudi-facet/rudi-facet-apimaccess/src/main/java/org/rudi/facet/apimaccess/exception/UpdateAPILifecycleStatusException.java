package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.APILifecycleStatusAction;

public class UpdateAPILifecycleStatusException extends APIManagerException {

	private static final long serialVersionUID = 3710440532188760969L;

	public UpdateAPILifecycleStatusException(String apiId, APILifecycleStatusAction action, Throwable e) {
		super(message(apiId, action), e);
	}

	private static String message(String apiId, APILifecycleStatusAction action) {
		return String.format("Failed to %s API with id = %s", action.getValue(), apiId);
	}
}
