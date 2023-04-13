package org.rudi.facet.apimaccess.helper.rest;

import org.rudi.facet.apimaccess.api.registration.ClientAccessKey;
import org.rudi.facet.apimaccess.api.registration.ClientRegistrationV017OperationAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class ClientRegistererForRudiAndAnonymous extends ClientRegisterer<ClientAccessKey> {

	ClientRegistererForRudiAndAnonymous(
			@Value("${apimanager.oauth2.client.provider.token-uri}") String tokenUri,
			@Value("${apimanager.oauth2.client.default.registration.scopes}") String[] scopes,
			ClientRegistrationV017OperationAPI clientRegistrationOperationAPI,
			@Value("${apimanager.oauth2.client.anonymous.use-domain-prefix-to-register:false}") boolean useDomainPrefixToRegisterAnonymous
	) {
		super(tokenUri, scopes, clientRegistrationOperationAPI, useDomainPrefixToRegisterAnonymous);
	}

}
