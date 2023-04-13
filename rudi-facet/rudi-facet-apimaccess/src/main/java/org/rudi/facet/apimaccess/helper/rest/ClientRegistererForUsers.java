package org.rudi.facet.apimaccess.helper.rest;

import java.util.List;

import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.registration.Application;
import org.rudi.facet.apimaccess.api.registration.OAuth2DynamicClientRegistrationOperationAPI;
import org.rudi.facet.apimaccess.api.registration.RegistrationRequestV11;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

@Component
class ClientRegistererForUsers extends ClientRegisterer<Application> {

	public ClientRegistererForUsers(
			@Value("${apimanager.oauth2.client.provider.token-uri}") String tokenUri,
			@Value("${apimanager.oauth2.client.default.registration-v1.1.scopes:" + APIManagerProperties.Scopes.INTERNAL_SUBSCRIBER + "}") String[] scopes,
			OAuth2DynamicClientRegistrationOperationAPI clientRegistrationOperationAPI
	) {
		super(tokenUri, scopes, clientRegistrationOperationAPI, false);
	}

	@Override
	public RegistrationRequestV11 buildRegistrationRequest(String username) {
		return RegistrationRequestV11.builder()
				.clientName(username)
				.grantTypes(List.of(AuthorizationGrantType.PASSWORD.getValue(), AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), AuthorizationGrantType.REFRESH_TOKEN.getValue()))
				.clientId(null) // null pour laisser WSO2 générer lui-même un client_id
				.clientSecret(null) // null pour laisser WSO2 générer lui-même un client_password
				.build();
	}

}
