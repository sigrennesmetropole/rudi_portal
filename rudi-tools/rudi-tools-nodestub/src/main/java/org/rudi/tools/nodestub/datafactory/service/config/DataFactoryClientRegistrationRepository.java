package org.rudi.tools.nodestub.datafactory.service.config;

import java.util.HashMap;
import java.util.Map;

import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Qualifier("dataFactoryClientRegistrationRepository")
@Slf4j
public class DataFactoryClientRegistrationRepository implements ReactiveClientRegistrationRepository {

	private final Map<String, ClientRegistration> clients = new HashMap<>();

	public DataFactoryClientRegistrationRepository(DataFactoryConfiguration dataFactoryConfiguration) {
		this.clients.put("datafactory", dataFactoryConfiguration.buildClientRegistration());
	}

	@Override
	public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
		try {
			return Mono.justOrEmpty(clients.get(registrationId));
		} catch (CacheLoadingException e) {
			log.error("Erreur lors de la récupération des clés de connexion à WSO2", e);
			return Mono.empty();
		}
	}

}
