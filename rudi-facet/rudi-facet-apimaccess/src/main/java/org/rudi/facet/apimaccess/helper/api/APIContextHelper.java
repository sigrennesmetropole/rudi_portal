package org.rudi.facet.apimaccess.helper.api;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.bean.APIDescription;
import org.rudi.facet.dataset.bean.InterfaceContract;

public class APIContextHelper {
	private static final String CONTEXT_SEPARATOR = "/";

	private APIContextHelper() {
	}

	public static String getInterfaceContractFromContext(String context) {
		return StringUtils.substringAfterLast(context, CONTEXT_SEPARATOR);
	}

	public static String buildAPIContext(APIDescription apiDescription) {
		return StringUtils.join(Arrays.asList(
				CONTEXT_SEPARATOR + "datasets",
				apiDescription.getMediaUuid().toString(),
				InterfaceContract.fromCode(apiDescription.getInterfaceContract()).getUrlPath()
		), CONTEXT_SEPARATOR);
	}

}
