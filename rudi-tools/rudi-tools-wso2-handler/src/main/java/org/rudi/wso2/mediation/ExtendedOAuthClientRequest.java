package org.rudi.wso2.mediation;

import java.util.Base64;
import java.util.Map;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthMessage;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.parameters.BodyURLEncodedParametersApplier;
import org.apache.oltu.oauth2.common.parameters.OAuthParametersApplier;

class ExtendedOAuthClientRequest extends OAuthClientRequest {
	protected ExtendedOAuthClientRequest(String url) {
		super(url);
	}

	public static TokenRequestBuilder tokenLocation(String url) {
		return new TokenRequestBuilder(url);
	}

	public static class TokenRequestBuilder extends OAuthClientRequest.TokenRequestBuilder {

		protected TokenRequestBuilder(String url) {
			super(url);
		}

		@Override
		public TokenRequestBuilder setGrantType(GrantType grantType) {
			return (TokenRequestBuilder) super.setGrantType(grantType);
		}

		@Override
		public TokenRequestBuilder setScope(String scope) {
			return (TokenRequestBuilder) super.setScope(scope);
		}

		@Override
		public TokenRequestBuilder setClientId(String clientId) {
			return (TokenRequestBuilder) super.setClientId(clientId);
		}

		@Override
		public TokenRequestBuilder setClientSecret(String secret) {
			return (TokenRequestBuilder) super.setClientSecret(secret);
		}

		ExtendedOAuthClientRequest buildHeaderAndBodyMessage() throws OAuthSystemException {
			OAuthClientRequest request = new ExtendedOAuthClientRequest(this.url);
			this.applier = new BasicHeaderAndBodyURLEncodedParametersApplier();
			return (ExtendedOAuthClientRequest) this.applier.applyOAuthParameters(request, this.parameters);
		}
	}

	/**
	 * Source : org.apache.oltu.oauth2.client.request.ClientHeaderParametersApplier
	 */
	private static class BasicHeaderAndBodyURLEncodedParametersApplier implements OAuthParametersApplier {
		private final BodyURLEncodedParametersApplier bodyURLEncodedParametersApplier = new BodyURLEncodedParametersApplier();

		@Override
		public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) throws OAuthSystemException {
			applyHeader(message, params);
			applyBody(message, params);
			return message;
		}

		private void applyHeader(OAuthMessage message, Map<String, Object> params) {
			final var header = OAuthUtils.encodeAuthorizationBasicHeader(params);
			message.addHeader("Authorization", header);
		}

		private void applyBody(OAuthMessage message, Map<String, Object> params) throws OAuthSystemException {
			bodyURLEncodedParametersApplier.applyOAuthParameters(message, params);
		}
	}

	/**
	 * Source : org.apache.oltu.oauth2.common.utils.OAuthUtils
	 */
	private static class OAuthUtils {

		public static String encodeAuthorizationBasicHeader(Map<String, Object> entries) {

			final var clientId = entries.get("client_id");
			if (clientId == null) {
				throw new IllegalArgumentException("Missing client_id parameter.");
			}

			final var clientSecret = entries.get("client_secret");
			if (clientSecret == null) {
				throw new IllegalArgumentException("Missing client_secret parameter.");
			}

			final String base64Credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

			return "Basic " + base64Credentials;
		}

	}
}
