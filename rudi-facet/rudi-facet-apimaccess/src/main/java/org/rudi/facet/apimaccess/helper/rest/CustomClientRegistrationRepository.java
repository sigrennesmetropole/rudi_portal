package org.rudi.facet.apimaccess.helper.rest;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.rudi.facet.apimaccess.exception.APIManagerHttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.List;

import static org.rudi.facet.apimaccess.constant.BeanIds.API_MACCESS_CACHE_CLIENT_REGISTRATION;

@Component
public class CustomClientRegistrationRepository implements ReactiveClientRegistrationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomClientRegistrationRepository.class);

    private final String registrationUri;
    private final String tokenUri;
    private final String[] defaultScopes;

    private final Cache<String, ClientRegistration> cache;

    @SuppressWarnings("java:S107")
    public CustomClientRegistrationRepository(@Value("${apimanager.oauth2.client.provider.token-uri}") String tokenUri,
                                              @Value("${apimanager.oauth2.client.registration.uri}") String registrationUri,
                                              @Value("${apimanager.oauth2.client.admin.registration.id}") String adminRegistrationId,
                                              @Value("${apimanager.oauth2.client.admin.registration.scopes}") String[] adminScopes,
                                              @Value("${apimanager.oauth2.client.admin.registration.client-id}") String adminClientId,
                                              @Value("${apimanager.oauth2.client.admin.registration.client-secret}") String adminClientSecret,
                                              @Value("${apimanager.oauth2.client.default.registration.scopes}") String[] defaultScopes,
                                              @Qualifier(API_MACCESS_CACHE_CLIENT_REGISTRATION) Cache<String, ClientRegistration> cache) {
        this.tokenUri = tokenUri;
        this.registrationUri = registrationUri;
        this.defaultScopes = defaultScopes;

        this.cache = cache;

        ClientRegistration adminClientRegistration = ClientRegistration.withRegistrationId(adminRegistrationId)
                .tokenUri(tokenUri)
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .scope(adminScopes)
                .build();
        this.cache.put(adminRegistrationId, adminClientRegistration);
    }

    @Override
    public Mono<ClientRegistration> findByRegistrationId(String registrationId) {
        try {
            ClientRegistration clientRegistration = cache.get(registrationId);
            return Mono.justOrEmpty(clientRegistration);
        } catch (CacheLoadingException e) {
            LOGGER.error("Erreur lors de la récupération des clés de connexion à WSO2", e);
            return Mono.empty();
        }
    }

    public ClientRegistration addClientRegistration(String username, String password) throws SSLException {
        ClientRegistration clientRegistration = buildClientRegistration(username, password);
        cache.put(username, clientRegistration);
        return clientRegistration;
    }

    public void addClientRegistration(String registrationId, ClientAccessKey clientAccessKey) {
        if (clientAccessKey != null) {
            cache.put(registrationId, buildClientRegistration(registrationId, clientAccessKey));
        }
    }

	private ClientRegistration buildClientRegistration(String username, String password) throws SSLException {
		ClientAccessPayload clientAccessPayload = ClientAccessPayload.builder()
				.callbackUrl("www.google.lk")
				.clientName(username)
				.owner(username)
				.grantType(StringUtils.join(List.of(AuthorizationGrantType.PASSWORD.getValue(), AuthorizationGrantType.CLIENT_CREDENTIALS.getValue(), AuthorizationGrantType.REFRESH_TOKEN.getValue()), " "))
				.saasApp(true)
				.build();

		SslContext sslContext = SslContextBuilder
				.forClient()
				.trustManager(InsecureTrustManagerFactory.INSTANCE)
				.build();
		HttpClient httpClient = HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

		final WebClient webClient = WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.filter(APIManagerHttpException.errorHandlingFilter())
				.build();
		return webClient
				.post()
				.uri(registrationUri)
				.headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(clientAccessPayload), ClientAccessPayload.class)
				.exchange()
				.flatMap((ClientResponse clientResponse) ->
						clientResponse.bodyToMono(ClientAccessKey.class).map(clientAccessKey ->
								buildClientRegistration(username, clientAccessKey)
						))
				.block();
	}

    private ClientRegistration buildClientRegistration(String registrationId, ClientAccessKey clientAccessKey) {
        return ClientRegistration.withRegistrationId(registrationId)
                .tokenUri(tokenUri)
                .clientId(clientAccessKey.getClientId())
                .clientSecret(clientAccessKey.getClientSecret())
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(defaultScopes)
                .build();
    }
}
