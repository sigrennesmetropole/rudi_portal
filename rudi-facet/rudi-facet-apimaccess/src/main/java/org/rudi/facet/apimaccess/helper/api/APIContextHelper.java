package org.rudi.facet.apimaccess.helper.api;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.bean.APIDescription;

public class APIContextHelper {
	private static final String CONTEXT_SEPARATOR = "/";

	private APIContextHelper() {
	}

	public static String getInterfaceContractFromContext(String context) {
		return StringUtils.substringAfterLast(context, CONTEXT_SEPARATOR);
	}

	public static String buildAPIContext(APIDescription apiDescription) {
		return CONTEXT_SEPARATOR + "datasets" + CONTEXT_SEPARATOR + apiDescription.getMediaUuid() + CONTEXT_SEPARATOR + apiDescription.getInterfaceContract();
	}

}
