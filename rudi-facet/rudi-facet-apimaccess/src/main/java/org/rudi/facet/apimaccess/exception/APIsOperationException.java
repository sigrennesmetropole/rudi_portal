package org.rudi.facet.apimaccess.exception;

import org.rudi.facet.apimaccess.bean.APISearchCriteria;
import org.wso2.carbon.apimgt.rest.api.publisher.API;

public class APIsOperationException extends APIManagerException {

	private static final long serialVersionUID = 7790372015026801582L;

	public APIsOperationException(API api, Throwable cause) {
		super(message(api), cause);
	}

	public APIsOperationException(APISearchCriteria apiSearchCriteria, Throwable cause) {
		super(message(apiSearchCriteria), cause);
	}

	private static String message(API api) {
		return String.format("API operation failed for API with name %s", api.getName());
	}

	private static String message(APISearchCriteria apiSearchCriteria) {
		return String.format("API search failed for criteria : %s", apiSearchCriteria);
	}

}
