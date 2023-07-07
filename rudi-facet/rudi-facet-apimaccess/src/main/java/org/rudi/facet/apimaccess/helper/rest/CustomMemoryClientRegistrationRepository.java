package org.rudi.facet.apimaccess.helper.rest;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLException;

import org.ehcache.Cache;
import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.registration.ClientRegistrationResponse;
import org.rudi.facet.apimaccess.exception.BuildClientRegistrationException;
import org.rudi.facet.apimaccess.exception.GetClientRegistrationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_CACHE_CLIENT_REGISTRATION;

@Component
@Slf4j
@ConditionalOnProperty(name = "apimanager.oauth2.client.registration.internal", havingValue = "true")
public class CustomMemoryClientRegistrationRepository implements RudiClientRegistrationRepository {

	private final Cache<String, ClientRegistration> cache;
	private final APIManagerProperties properties;
	private final ClientRegistererForRudiAndAnonymous clientRegistererForRudiAndAnonymous;
	private final ClientRegistererForUsers clientRegistererForUsers;

	@SuppressWarnings("java:S107")
	public CustomMemoryClientRegistrationRepository(
			@Qualifier(API_MACCESS_CACHE_CLIENT_REGISTRATION) Cache<String, ClientRegistration> cache,
			APIManagerProperties properties,
			ClientRegistererForAdmin clientRegistererForAdmin,
			ClientRegistererForRudiAndAnonymous clientRegistererForRudiAndAnonymous,
			ClientRegistererForUsers clientRegistererForUsers
	) {
		this.cache = cache;
		this.properties = properties;
		this.clientRegistererForRudiAndAnonymous = clientRegistererForRudiAndAnonymous;
		this.clientRegistererForUsers = clientRegistererForUsers;

		this.addClientRegistration(clientRegistererForAdmin.buildClientRegistration());
	}

	private void addClientRegistration(ClientRegistration clientRegistration) {
		this.cache.put(clientRegistration.getRegistrationId(), clientRegistration);
	}

	@Override
	public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
		try {
			return Mono.justOrEmpty(cache.get(registrationId));
		} catch (CacheLoadingException e) {
			log.error("Erreur lors de la récupération des clés de connexion à WSO2", e);
			return Mono.empty();
		}
	}

	@Nullable
	@Override
	public ClientRegistration findByUsername(String username) throws GetClientRegistrationException {
		final var registrationId = ClientRegisterer.getRegistrationId(username);
		if (registrationId == null) {
			throw new GetClientRegistrationException(username, new NullPointerException());
		}
		return findByRegistrationId(registrationId).block();
	}

	@Nullable
	@Override
	public ClientRegistration findByUsernameAndPassword(String username, String password) throws SSLException, GetClientRegistrationException {
		final var cachedRegistration = findByUsername(username);
		if (cachedRegistration == null) {
			throw new GetClientRegistrationException(username, new NullPointerException());
		}
		if (cachedRegistration != null) {
			return cachedRegistration;
		}

		final var clientRegisterer = getClientRegistererFor(username);
		try {
			final var remoteRegistration = clientRegisterer.getRegistration(username, password);
			cache.put(username, remoteRegistration);
			return remoteRegistration;
		} catch (GetClientRegistrationException e) {
			return null;
		}
	}

	/**
	 * Renvoie l'enregistrement client s'il existe dans le cache, sinon réalise l'enregistrement
	 */
	@Nonnull
	@Override
	public ClientRegistration findRegistrationOrRegister(String username, String password) throws BuildClientRegistrationException, SSLException, GetClientRegistrationException {
		val registration = findByUsername(username);
		if (registration == null) {
			return register(username, password);
		}
		return registration;
	}

	@Override
	public ClientRegistration register(String username, String password) throws SSLException, BuildClientRegistrationException, GetClientRegistrationException {
		final var clientRegisterer = getClientRegistererFor(username);
		final var clientRegistration = clientRegisterer.register(username, password);
		addClientRegistration(clientRegistration);
		return clientRegistration;
	}

	@Override
	public void addClientRegistration(String username, ClientRegistrationResponse clientAccessKey) {
		if (clientAccessKey != null) {
			final var clientRegisterer = getClientRegistererFor(username);
			final var registrationId = ClientRegisterer.getRegistrationId(username);
			final var clientRegistration = clientRegisterer.buildClientRegistration(registrationId, clientAccessKey);
			addClientRegistration(clientRegistration);
		}
	}

	private ClientRegisterer<? extends ClientRegistrationResponse> getClientRegistererFor(String username) {
		if (properties.isAdminOrAnonymous(username)) {
			return clientRegistererForRudiAndAnonymous;
		} else {
			return clientRegistererForUsers;
		}
	}

}
