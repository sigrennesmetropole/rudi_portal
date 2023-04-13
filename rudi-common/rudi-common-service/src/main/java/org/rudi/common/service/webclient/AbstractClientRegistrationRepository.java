package org.rudi.common.service.webclient;

import java.util.HashMap;
import java.util.Map;

import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public abstract class AbstractClientRegistrationRepository implements ReactiveClientRegistrationRepository {
	protected final Map<String, ClientRegistration> clients = new HashMap<>();

	@Override
	public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
		try {
			return Mono.justOrEmpty(clients.get(registrationId));
		} catch (CacheLoadingException e) {
			log.error("Erreur lors de la récupération des clés de connexion à l'API cible", e);
			return Mono.empty();
		}
	}
}
