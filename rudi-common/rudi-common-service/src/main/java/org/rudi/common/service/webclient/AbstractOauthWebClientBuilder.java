package org.rudi.common.service.webclient;

import javax.net.ssl.SSLException;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

public abstract class AbstractOauthWebClientBuilder {

	private final AbstractClientRegistrationRepository abstractClientRegistrationRepository;

	private final HttpClientHelper httpClientHelper;

	protected AbstractOauthWebClientBuilder(AbstractClientRegistrationRepository abstractClientRegistrationRepository,
			HttpClientHelper httpClientHelper) {
		this.abstractClientRegistrationRepository = abstractClientRegistrationRepository;
		this.httpClientHelper = httpClientHelper;
	}

	protected WebClient.Builder oauthWebClientBuilder(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder)
			throws SSLException {
		ReactiveOAuth2AuthorizedClientService reactiveOAuth2AuthorizedClientService = new InMemoryReactiveOAuth2AuthorizedClientService(
				abstractClientRegistrationRepository);

		final HttpClient httpClient = httpClientHelper.createReactorHttpClient(isTrustAllCerts(), false, false);

		final var clientCredentialsReactiveOAuth2AuthorizedClientProvider = new ClientCredentialsReactiveOAuth2AuthorizedClientProvider();
		final var webClientReactiveClientCredentialsTokenResponseClient = new WebClientReactiveClientCredentialsTokenResponseClient();
		webClientReactiveClientCredentialsTokenResponseClient
				.setWebClient(WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build());
		clientCredentialsReactiveOAuth2AuthorizedClientProvider
				.setAccessTokenResponseClient(webClientReactiveClientCredentialsTokenResponseClient);

		final var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				abstractClientRegistrationRepository, reactiveOAuth2AuthorizedClientService);

		authorizedClientManager.setAuthorizedClientProvider(clientCredentialsReactiveOAuth2AuthorizedClientProvider);

		final var oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		final var objectMapper = jackson2ObjectMapperBuilder.build();

		return WebClient.builder().filter(oauthFilter).codecs(clientDefaultCodecsConfigurer -> {
			clientDefaultCodecsConfigurer.defaultCodecs()
					.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
			clientDefaultCodecsConfigurer.defaultCodecs()
					.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
		}).clientConnector(new ReactorClientHttpConnector(httpClient));
	}

	protected abstract WebClient webClient(WebClient.Builder oauthWebClientBuilder);

	protected abstract boolean isTrustAllCerts();
}
