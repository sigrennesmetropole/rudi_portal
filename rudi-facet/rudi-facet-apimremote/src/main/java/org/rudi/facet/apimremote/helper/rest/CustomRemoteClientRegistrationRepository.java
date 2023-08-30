package org.rudi.facet.apimremote.helper.rest;

import javax.net.ssl.SSLException;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rudi.facet.acl.bean.AccessKeyDto;
import org.rudi.facet.acl.bean.ClientRegistrationDto;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.api.registration.ClientRegistrationResponse;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.rudi.facet.apimaccess.helper.rest.RudiClientRegistrationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "apimanager.oauth2.client.registration.internal", havingValue = "false")
public class CustomRemoteClientRegistrationRepository implements RudiClientRegistrationRepository {

	private final ACLHelper aclHelper;

	@Nullable
	@Override
	public ClientRegistration findByUsername(String username) throws GetClientRegistrationException {
		val registration = aclHelper.getClientRegistrationByLogin(username);
		if (registration == null) { // On part du principe que la registration est faite dans ACL et pas dans les remote
			throw new GetClientRegistrationException(username, new NullPointerException());
		}
		return convertToEntity(registration);
	}

	@Nullable
	@Override
	public ClientRegistration findByUsernameAndPassword(String username, String password)
			throws SSLException, GetClientRegistrationException, BuildClientRegistrationException {
		return findRegistrationOrRegister(username, password);
	}

	@NotNull
	@Override
	public ClientRegistration findRegistrationOrRegister(String username, String password)
			throws BuildClientRegistrationException, SSLException, GetClientRegistrationException {
		// On fait la registration dans ACL et pas dans les remote
		val clientRegistrationDto = aclHelper.findRegistrationOrRegister(username, password);
		return convertToEntity(clientRegistrationDto);
	}

	@Override
	public ClientRegistration register(String username, String password)
			throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		throw new UnsupportedOperationException("Méthode non implémentée. A implementer si le besoin est vérifié");
	}

	@Override
	public void addClientRegistration(String username, ClientRegistrationResponse clientAccessKey) {
		val accessKey = new AccessKeyDto();
		accessKey.setClientId(clientAccessKey.getClientId());
		accessKey.setClientSecret(clientAccessKey.getClientSecret());
		aclHelper.addClientRegistration(username, accessKey);
	}

	@Override
	public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
		val registration = aclHelper.getClientRegistrationByLogin(registrationId);
		if (registration == null) {
			try {
				throw new GetClientRegistrationException(registrationId, new NullPointerException());
			} catch (GetClientRegistrationException e) {
				e.printStackTrace();
			}
		}
		return Mono.justOrEmpty(convertToEntity(registration));
	}

	/**
	 * Conversion d'un DTO vers un ClientRegistration
	 *
	 * @param registrationDto DTO à convertir
	 * @return
	 */
	private ClientRegistration convertToEntity(ClientRegistrationDto registrationDto) {
		val builder = ClientRegistration.withRegistrationId(registrationDto.getRegistrationId())
				.clientId(registrationDto.getClientId()).clientSecret(registrationDto.getClientSecret())
				.clientName(registrationDto.getClientName()).scope(registrationDto.getScopes())
				.redirectUri(registrationDto.getRedirectUriTemplate())
				.tokenUri(registrationDto.getProviderDetails().getTokenUri())
				.jwkSetUri(registrationDto.getProviderDetails().getJwkSetUri())
				.userInfoUri(registrationDto.getProviderDetails().getUserInfoEndpoint().getUri())
				.userNameAttributeName(
						registrationDto.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName())
				.userInfoAuthenticationMethod(AuthenticationMethod.HEADER)
				.authorizationUri(registrationDto.getProviderDetails().getAuthorizationUri());

		addGrantType(builder, registrationDto);
		addAuthenticationMethod(builder, registrationDto);
		return builder.build();
	}

	private void addGrantType(ClientRegistration.Builder builder, ClientRegistrationDto registrationDto) {
		AuthorizationGrantType grantType;
		switch (registrationDto.getAuthorizationGrantType().getValue()) {
		case PASSWORD:
			grantType = AuthorizationGrantType.PASSWORD;
			break;
		case CLIENT_CREDENTIALS:
			grantType = AuthorizationGrantType.CLIENT_CREDENTIALS;
			break;
		default:
			grantType = AuthorizationGrantType.REFRESH_TOKEN;
		}
		builder.authorizationGrantType(grantType);
	}

	private void addAuthenticationMethod(ClientRegistration.Builder builder, ClientRegistrationDto registrationDto) {
		ClientAuthenticationMethod authenticationMethod;
		switch (registrationDto.getClientAuthenticationMethod().getValue()) {
		case BASIC:
			authenticationMethod = ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
			break;
		case POST:
			authenticationMethod = ClientAuthenticationMethod.CLIENT_SECRET_POST;
			break;
		default:
			authenticationMethod = ClientAuthenticationMethod.NONE;
		}
		builder.clientAuthenticationMethod(authenticationMethod);
	}
}
