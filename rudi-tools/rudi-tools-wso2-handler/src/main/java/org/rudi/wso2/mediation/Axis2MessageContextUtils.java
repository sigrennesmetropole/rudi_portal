package org.rudi.wso2.mediation;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.springframework.http.HttpHeaders;

final class Axis2MessageContextUtils {
	private static final String HEADER_SELFDATA_TOKEN = "X-SELFDATA-TOKEN";

	private Axis2MessageContextUtils() {
	}

	static String getContentType(MessageContext axis2MC) {
		return (String) axis2MC.getProperty(Constants.Configuration.CONTENT_TYPE);
	}

	static void setContentType(MessageContext axis2MC, final String mimeType) {
		axis2MC.setProperty(Constants.Configuration.CONTENT_TYPE, mimeType);
	}

	@Nullable
	public static String getContentDisposition(MessageContext axis2MC) {
		final Map<String, String> transportHeaders = getTransportHeaders(axis2MC);
		return transportHeaders.get(HttpHeaders.CONTENT_DISPOSITION);
	}

	public static Map<String, String> getTransportHeaders(MessageContext axis2MC) {
		//noinspection unchecked : classe MessageContext de axis2 non modifiable
		return (Map<String, String>) axis2MC.getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
	}

	public static void setContentDisposition(MessageContext axis2MC, final String contentDisposition) {
		final Map<String, String> transportHeaders = getTransportHeaders(axis2MC);
		transportHeaders.put(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
	}

	public static void setSelfdataTokenHeader(MessageContext axis2MessageContext, UUID selfdataToken) {
		final Map<String, String> transportHeaders = getTransportHeaders(axis2MessageContext);
		transportHeaders.put(HEADER_SELFDATA_TOKEN, selfdataToken.toString());
	}
}
