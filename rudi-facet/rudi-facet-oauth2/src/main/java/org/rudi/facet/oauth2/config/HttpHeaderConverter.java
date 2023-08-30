package org.rudi.facet.oauth2.config;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.endpoint.AbstractOAuth2AuthorizationGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * 
 * @author FNI18300
 *
 */
public class HttpHeaderConverter<T extends AbstractOAuth2AuthorizationGrantRequest> {
	/**
	 * Populates the headers for the token request.
	 * 
	 * @param grantRequest the grant request
	 * @return the headers populated for the token request
	 */
	public HttpHeaders populateTokenRequestHeaders(T grantRequest) {
		HttpHeaders headers = new HttpHeaders();
		ClientRegistration clientRegistration = clientRegistration(grantRequest);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.equals(clientRegistration.getClientAuthenticationMethod())
				|| ClientAuthenticationMethod.BASIC.equals(clientRegistration.getClientAuthenticationMethod())) {
			String clientId = encodeClientCredential(clientRegistration.getClientId());
			String clientSecret = encodeClientCredential(clientRegistration.getClientSecret());
			headers.setBasicAuth(clientId, clientSecret);
		}
		return headers;
	}

	private ClientRegistration clientRegistration(T grantRequest) {
		return grantRequest.getClientRegistration();
	}

	private String encodeClientCredential(String value) {
		return value;
	}
}
