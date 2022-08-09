package org.rudi.wso2.mediation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.model.API;

/**
 * Compare les clés publiques en se basant sur leur URL.
 */
class PublicKeyURLComparator implements PublicKeyComparator {
	static final String PUBLIC_KEY_URL_PROPERTY = "publicKeyURL";
	static final String PUBLIC_KEY_URL_ADDITIONAL_PROPERTY = "public_key_url";
	private static final Log LOGGER = LogFactory.getLog(PublicKeyURLComparator.class);

	@Override
	public boolean usesSamePublicKey(EncryptedMediaHandler encryptedMediaHandler, API engagedApi) {
		final var rawPortalPublicKeyURL = (String) encryptedMediaHandler.getProperties().get(PUBLIC_KEY_URL_PROPERTY);
		final var rawApiPublicKeyURL = AdditionalPropertiesUtil.getAdditionalProperty(PUBLIC_KEY_URL_ADDITIONAL_PROPERTY, engagedApi);

		LOGGER.debug("Handler." + PUBLIC_KEY_URL_PROPERTY + " = " + rawPortalPublicKeyURL);
		LOGGER.debug("API." + PUBLIC_KEY_URL_ADDITIONAL_PROPERTY + " = " + rawApiPublicKeyURL);

		final var portalPublicKeyURL = normalizeURL(rawPortalPublicKeyURL);
		final var apiPublicKeyURL = normalizeURL(rawApiPublicKeyURL);

		LOGGER.debug("Normalized Handler." + PUBLIC_KEY_URL_PROPERTY + " = " + portalPublicKeyURL);
		LOGGER.debug("Normalized API." + PUBLIC_KEY_URL_ADDITIONAL_PROPERTY + " = " + apiPublicKeyURL);

		final var equals = Objects.equals(portalPublicKeyURL, apiPublicKeyURL);

		LOGGER.debug("Public keys matches: " + equals);

		return equals;
	}

	@Nullable
	private String normalizeURL(@Nullable String url) {
		if (url == null) {
			return null;
		}
		final var urlWithoutPort = url.replaceAll("^(.+):\\d+/", "$1/");
		final var urlWithoutProtocolAndPort = urlWithoutPort.replaceAll("^.+:(?://)?(.+)/", "$1/");

		try {
			final var hostAddress = getHostAddress(urlWithoutProtocolAndPort);
			final var path = StringUtils.substringAfter(urlWithoutProtocolAndPort, "/");
			final var urlWithHostAddress = hostAddress + "/" + path;
			return urlWithHostAddress.toLowerCase();
		} catch (UnknownHostException e) {
			LOGGER.warn("Cannot get host address from URL without protocol: " + urlWithoutProtocolAndPort + ". Continuing using raw URL without host resolution...", e);
			return urlWithoutProtocolAndPort;
		}
	}

	private String getHostAddress(final String urlWithoutProtocolAndPort) throws UnknownHostException {
		final var host = StringUtils.substringBefore(urlWithoutProtocolAndPort, "/");
		final var inetAddress = InetAddress.getByName(host);
		return inetAddress.getHostAddress();
	}
}
