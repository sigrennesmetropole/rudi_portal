package org.rudi.facet.apimaccess.helper.rest;

import java.util.List;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLException;

import org.apache.commons.lang3.StringUtils;
import org.rudi.facet.apimaccess.api.registration.AbstractClientRegistrationOperationAPI;
import org.rudi.facet.apimaccess.api.registration.ApplicationAlreadyExistException;
import org.rudi.facet.apimaccess.api.registration.ClientRegistrationResponse;
import org.rudi.facet.apimaccess.api.registration.RegistrationRequest;
import org.rudi.facet.apimaccess.api.registration.RegistrationRequestV017;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
abstract class ClientRegisterer<S extends ClientRegistrationResponse> {

	protected final String tokenUri;
	protected final String[] scopes;
	protected final AbstractClientRegistrationOperationAPI<S> clientRegistrationperationAPI;

	/**
	 * Applique le patch pour faire fonctionner le register de l'utilisateur "anonymous" dans le cas où WSO2 le traite
	 * injustement en tant que "PRIMARY/anonymous" au lieu de "RUDI/anonymous".
	 * Cf. erreur "RegistrationServiceImpl User anonymous does not have any of subscribe/create/publish privileges to create an OAuth app"
	 * dans les commentaires de la RUDI-3042.
	 */
	private final boolean useDomainPrefixToRegisterAnonymous;

	public static String getRegistrationId(String username) {
		return username;
	}

	@Nonnull
	public ClientRegistration getRegistration(String username, String password) throws SSLException, GetClientRegistrationException {
		final var registrationResponse = clientRegistrationperationAPI.getRegistration(username, password);
		final var registrationId = getRegistrationId(username);
		return buildClientRegistration(registrationId, registrationResponse);
	}

	public ClientRegistration register(String username, String password) throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		S registrationResponse;
		final var usernameForApiManager = "anonymous".equals(username) ? "RUDI/anonymous" : username;
		final var registrationRequest = buildRegistrationRequest(usernameForApiManager);
		try {
			// On tente d'abord un register, même s'il a déjà été fait, avant de faire un getRegistration
			// car l'exception précise clairement que l'application existe déjà (cf ApplicationAlreadyExistException).
			// Si on faisait un getRegistration alors que l'application n'existe pas encore
			// une erreur 401 serait lancée sans préciser la véritable cause de l'erreur.
			registrationResponse = clientRegistrationperationAPI.register(usernameForApiManager, password, registrationRequest);
		} catch (BuildClientRegistrationException e) {
			final var cause = e.getCause();
			if (cause instanceof ApplicationAlreadyExistException) {

				final var applicationAlreadyExistException = (ApplicationAlreadyExistException) cause;
				log.info("Application already exists for clientName " + applicationAlreadyExistException.getClientName());
				registrationResponse = clientRegistrationperationAPI.getRegistration(username, password);
			} else {
				throw e;
			}
		}
		final var registrationId = getRegistrationId(username);
		return buildClientRegistration(registrationId, registrationResponse);
	}

	public RegistrationRequest buildRegistrationRequest(String username) {
		final var clientName = StringUtils.contains(username, "/") ? StringUtils.substringAfterLast(username, "/") : username;
		return RegistrationRequestV017.builder()
				.callbackUrl("www.google.lk")
				.clientName(clientName)
				.owner(username)
				.grantType(StringUtils.join(List.of(AuthorizationGrantType.PASSWORD.getValue(), AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), AuthorizationGrantType.REFRESH_TOKEN.getValue()), " "))
				.saasApp(true)
				.build();
	}

	public ClientRegistration buildClientRegistration(String registrationId, ClientRegistrationResponse clientRegistrationResponse) {
		return ClientRegistration.withRegistrationId(registrationId)
				.tokenUri(tokenUri)
				.clientId(clientRegistrationResponse.getClientId())
				.clientSecret(clientRegistrationResponse.getClientSecret())
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope(scopes)
				.build();
	}

}
