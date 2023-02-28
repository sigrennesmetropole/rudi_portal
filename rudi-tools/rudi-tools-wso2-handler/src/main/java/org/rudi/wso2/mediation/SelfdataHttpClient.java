package org.rudi.wso2.mediation;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;

/**
 * Source : org.wso2.carbon.ganalytics.publisher.GoogleAnalyticsDataPublisher
 */
@CommonsLog
@RequiredArgsConstructor
class SelfdataHttpClient {

	private final SelfdataProperties selfdataProperties;
	private final SelfdataOauth2Properties selfdataOauth2Properties;

	public UUID getSelfdataToken(final UUID datasetUuid, final String login) {
		try {
			log.debug("User login which inititate handler: " + login);
			log.debug("Dataset uuid : " + datasetUuid);
			final var oAuthRequest = new OAuthBearerClientRequest(getMatchingTokenApiUri(login, datasetUuid).toString())
					.setAccessToken(retrieveAccessToken())
					.buildHeaderMessage();
			final var oAuthClient = new OAuthClient(new URLConnectionClient());
			final var resource = oAuthClient.resource(oAuthRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);
			final var quotedUuid = resource.getBody();
			return UUID.fromString(StringUtils.substringBetween(quotedUuid, "\""));
		} catch (OAuthSystemException | OAuthProblemException e) {
			throw new RudiServiceException("selfdata", e);
		}
	}

	private URI getMatchingTokenApiUri(String userLogin, UUID datasetUuid) {
		// Avant, faire un appel d'authent sur ACL avec le user ROBOT
		final var basePath = selfdataProperties.getBasePath();

		final Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("dataset-uuid", datasetUuid);
		pathParams.put("login", userLogin);
		final var matchingTokenPathForStringSubstitutor = selfdataProperties.getMatchingTokenPath().replace("{", "${");
		final var getMatchingTokenApiPath = StringSubstitutor.replace(matchingTokenPathForStringSubstitutor, pathParams);

		return URI.create(basePath + getMatchingTokenApiPath);
	}

	private String retrieveAccessToken() {
		try {
			log.debug("User which call selfdata microservice: " + selfdataOauth2Properties.getClientId());
			// https://cwiki.apache.org/confluence/display/OLTU/OAuth+2.0+Client+Quickstart
			final var request = ExtendedOAuthClientRequest
					.tokenLocation(selfdataOauth2Properties.getTokenUri())
					.setGrantType(GrantType.CLIENT_CREDENTIALS)
					.setScope(selfdataOauth2Properties.getScopeSeparatedWithSpaces())
					.setClientId(selfdataOauth2Properties.getClientId())
					.setClientSecret(selfdataOauth2Properties.getClientSecret())
					.buildHeaderAndBodyMessage();
			final var oAuthClient = new OAuthClient(new URLConnectionClient());
			final var oAuthJSONAccessTokenResponse = oAuthClient.accessToken(request, OAuth.HttpMethod.POST, OAuthJSONAccessTokenResponse.class);
			return oAuthJSONAccessTokenResponse.getAccessToken();
		} catch (OAuthSystemException | OAuthProblemException e) {
			throw new RudiServiceException("acl", e);
		}
	}

}
