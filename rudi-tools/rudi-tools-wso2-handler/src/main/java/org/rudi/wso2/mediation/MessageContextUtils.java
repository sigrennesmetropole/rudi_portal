package org.rudi.wso2.mediation;

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;

final class MessageContextUtils {
	private MessageContextUtils() {
	}

	public static String getAuthenticatedUserLogin(MessageContext messageContext) {
		final var userId = (String) messageContext.getProperty("api.ut.userId");
		return StringUtils.substringBeforeLast(StringUtils.substringAfter(userId, "/"), "@");
	}
}
