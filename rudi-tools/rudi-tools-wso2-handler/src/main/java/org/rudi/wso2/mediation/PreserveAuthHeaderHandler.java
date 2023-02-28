package org.rudi.wso2.mediation;

import org.apache.commons.lang3.StringUtils;
import org.apache.synapse.MessageContext;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Handler pour conserver le bearer token (ce que WSO2 ne fait pas par défaut).
 *
 * <ul>
 * <li><a href="https://github.com/athiththan11/Preserve-Auth-Header-Handler/blob/7617d30027fd89c8161eb9f4d89665e9dc0a8e1b/src/main/java/com/sample/handlers/PreserveAuthHeaderHandler.java">Source</a></li>
 * <li><a href="https://athiththan11.medium.com/preserve-authorization-header-in-wso2-api-manager-e9469e43d1c0">Article</a></li>
 * </ul>
 */
@CommonsLog
public class PreserveAuthHeaderHandler extends AbstractRudiHandler {
	private String authorizationHeader;

	@SuppressWarnings("unused")
	// Utilisé par WSO2 au chargement du Handler quand on définit la propriété "authorizationHeader" dans le velocity_template.xml
	public void setAuthorizationHeader(String authHeader) {
		this.authorizationHeader = authHeader;
	}

	@Override
	protected boolean engageRequest(MessageContext messageContext) {
		return true;
	}

	@Override
	protected void doHandleRequest(MessageContext messageContext) {
		preserveAuthHeader(messageContext);
	}

	private void preserveAuthHeader(MessageContext context) {
		log.debug("Extracting Auth Header: " + authorizationHeader);

		final var axis2MessageContext = getAxis2MessageContext(context);
		final var transportHeaders = Axis2MessageContextUtils.getTransportHeaders(axis2MessageContext);

		if (StringUtils.isNotBlank(authorizationHeader) && transportHeaders.containsKey(authorizationHeader)) {
			final var tokenContent = transportHeaders.get(authorizationHeader);

			log.debug("Extracted authorization header: " + authorizationHeader + " with value: " + tokenContent);

			context.setProperty("PRESERVE_AUTH_HEADER_HANDLER_HEADER", authorizationHeader);
			context.setProperty("PRESERVE_AUTH_HEADER_HANDLER_TOKEN", tokenContent);
		}
	}
}
