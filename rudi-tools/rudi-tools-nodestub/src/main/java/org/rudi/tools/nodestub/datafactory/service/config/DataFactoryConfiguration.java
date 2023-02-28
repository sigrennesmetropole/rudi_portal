package org.rudi.tools.nodestub.datafactory.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
public class DataFactoryConfiguration {

	public static final String DATAFACTORY_WEBCLIENT = "datafactory_webclient";

	@Value("${datafactory.oauth2.client.provider.token-uri}")
	private String tokenUri;
	@Value("${datafactory.oauth2.client.provider.authorization-uri}")
	private String authorizationUri;
	@Value("${datafactory.oauth2.client.registration.scopes}")
	private String[] scopes;
	@Value("${datafactory.oauth2.client.registration.client-name}")
	private String clientName;
	@Value("${datafactory.oauth2.client.registration.client-password}")
	private String clientPassword;
	@Value("${datafactory.oauth2.client.registration.client-id}")
	private String clientId;
	@Value("${datafactory.oauth2.client.registration.client-secret}")
	private String clientSecret;

	@Value("${datafactory.base-uri:https://api.rennesmetropole.fr/dfrm-test}")
	private String baseUri;
	@Value("${datafactory.mocked:false}")
	private boolean mocked = false;

	public ClientRegistration buildClientRegistration() {
		return ClientRegistration.withRegistrationId("datafactory").tokenUri(tokenUri)
				.authorizationUri(authorizationUri).clientId(clientId).clientName(clientName).clientSecret(clientSecret)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).scope(scopes).build();
	}

}
