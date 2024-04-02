package org.rudi.facet.apimaccess.helper.rest;

import org.rudi.facet.apimaccess.api.registration.Application;
import org.rudi.facet.apimaccess.api.registration.ClientRegistrationResponse;
import org.rudi.facet.apimaccess.api.registration.OAuth2DynamicClientRegistrationOperationAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

@Component
class ClientRegistererForAdmin extends ClientRegisterer<Application> {

	private final String adminRegistrationId;
	private final String adminClientId;
	private final String adminClientSecret;

	ClientRegistererForAdmin(@Value("${apimanager.oauth2.client.provider.token-uri}") String tokenUri,
			@Value("${apimanager.oauth2.client.admin.registration.scopes}") String[] scopes,
			@Value("${apimanager.oauth2.client.admin.registration.id}") String adminRegistrationId,
			@Value("${apimanager.oauth2.client.admin.registration.client-id}") String adminClientId,
			@Value("${apimanager.oauth2.client.admin.registration.client-secret}") String adminClientSecret,
			OAuth2DynamicClientRegistrationOperationAPI clientRegistrationOperationAPI) {
		super(tokenUri, scopes, clientRegistrationOperationAPI, false);
		this.adminRegistrationId = adminRegistrationId;
		this.adminClientId = adminClientId;
		this.adminClientSecret = adminClientSecret;
	}

	@Override
	public ClientRegistration buildClientRegistration(String registrationId,
			ClientRegistrationResponse clientRegistrationResponse) {
		return ClientRegistration.withRegistrationId(registrationId).tokenUri(tokenUri)
				.clientId(clientRegistrationResponse.getClientId())
				.clientSecret(clientRegistrationResponse.getClientSecret())
				.authorizationGrantType(AuthorizationGrantType.PASSWORD).scope(scopes).build();
	}

	public ClientRegistration buildClientRegistration() {
		final var adminClientAccessKey = new Application();
		adminClientAccessKey.setClientId(adminClientId);
		adminClientAccessKey.setClientSecret(adminClientSecret);
		return buildClientRegistration(adminRegistrationId, adminClientAccessKey);
	}
}
