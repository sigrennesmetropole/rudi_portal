package org.rudi.facet.apimaccess.helper.rest;

import org.rudi.facet.apimaccess.api.registration.Application;
import org.rudi.facet.apimaccess.api.registration.OAuth2DynamicClientRegistrationOperationAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class ClientRegistererForRudiAndAnonymous extends ClientRegisterer<Application> {

	ClientRegistererForRudiAndAnonymous(@Value("${apimanager.oauth2.client.provider.token-uri}") String tokenUri,
			@Value("${apimanager.oauth2.client.default.registration.scopes}") String[] scopes,
			OAuth2DynamicClientRegistrationOperationAPI clientRegistrationOperationAPI,
			@Value("${apimanager.oauth2.client.anonymous.use-domain-prefix-to-register:false}") boolean useDomainPrefixToRegisterAnonymous) {
		super(tokenUri, scopes, clientRegistrationOperationAPI, useDomainPrefixToRegisterAnonymous);
	}

}
