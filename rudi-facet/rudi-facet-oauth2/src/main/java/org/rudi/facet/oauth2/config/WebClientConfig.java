package org.rudi.facet.oauth2.config;

import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLException;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import reactor.netty.http.client.HttpClient;

/**
 * @author FNI18300
 */
public class WebClientConfig {

	public static final String REGISTRATION_ID = "rudi_module";

	@Getter
	@Value("${module.oauth2.trust-all-certs:false}")
	private boolean trustAllCerts;

	@Getter
	@Value("${module.oauth2.client-id}")
	private String clientId;

	@Getter
	@Value("${module.oauth2.client-secret}")
	private String clientSecret;

	@Getter
	@Value("${module.oauth2.provider-uri}")
	private String tokenUri;

	@Getter
	@Value("${module.oauth2.scope}")
	private String[] scopes;

	private HttpClientHelper httpClientHelper;

	public WebClientConfig(HttpClientHelper httpClientHelper) {
		super();
		this.httpClientHelper = httpClientHelper;
	}

	@Bean(name = "rudi_oauth2_repository")
	public ReactiveClientRegistrationRepository getRegistration() {
		ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
				.tokenUri(tokenUri).clientId(clientId).clientSecret(clientSecret)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).scope(scopes).build();
		return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
	}

	@Bean(name = "rudi_oauth2_client_service")
	public ReactiveOAuth2AuthorizedClientService clientService(
			@Qualifier("rudi_oauth2_repository") ReactiveClientRegistrationRepository clientRegistrationRepository) {
		return new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository);
	}

	@Bean(name = "rudi_oauth2_client_repository")
	public ServerOAuth2AuthorizedClientRepository clientRepository(
			@Qualifier("rudi_oauth2_client_service") ReactiveOAuth2AuthorizedClientService clientService) {
		return new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(clientService);
	}

	@Bean(name = "rudi_oauth2_httpclient")
	public HttpClient httpClient() throws SSLException {
		return httpClientHelper.createReactorHttpClient(trustAllCerts, false, false);
	}

	@Bean(name = "rudi_oauth2_client_manager")
	public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
			@Qualifier("rudi_oauth2_repository") ReactiveClientRegistrationRepository clientRegistrationRepository,
			@Qualifier("rudi_oauth2_client_service") ReactiveOAuth2AuthorizedClientService clientService,
			@Qualifier("rudi_oauth2_client_repository") ServerOAuth2AuthorizedClientRepository authorizedClientRepository,
			@Qualifier("rudi_oauth2_httpclient") HttpClient httpClient) {
		WebClientReactiveClientCredentialsTokenResponseClient webClientReactiveClientCredentialsTokenResponseClient = new WebClientReactiveClientCredentialsTokenResponseClient();
		HttpHeaderConverter<OAuth2ClientCredentialsGrantRequest> httpHeaderConverter = new HttpHeaderConverter<>();
		webClientReactiveClientCredentialsTokenResponseClient
				.setHeadersConverter(httpHeaderConverter::populateTokenRequestHeaders);
		webClientReactiveClientCredentialsTokenResponseClient
				.setWebClient(WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build());

		ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder
				.builder()
				.clientCredentials(
						b -> b.accessTokenResponseClient(webClientReactiveClientCredentialsTokenResponseClient))
				.build();

		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				clientRegistrationRepository, clientService);

		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
		return authorizedClientManager;
	}

	@LoadBalanced
	@Bean(name = "rudi_oauth2_builder")
	public WebClient.Builder webClientBuilder(
			@Qualifier("rudi_oauth2_client_manager") ReactiveOAuth2AuthorizedClientManager authorizedClientManager,
			@Qualifier("rudi_oauth2_httpclient") HttpClient httpClient) {
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauthFilter.setDefaultClientRegistrationId(REGISTRATION_ID);
		return WebClient.builder().filter(oauthFilter)
				.defaultHeaders(header -> header.setBasicAuth(clientId, clientSecret, StandardCharsets.UTF_8))
				.clientConnector(new ReactorClientHttpConnector(httpClient));
	}

	@Bean(name = "rudi_oauth2")
	public WebClient webClient(@Qualifier("rudi_oauth2_builder") WebClient.Builder builder) {
		return builder.build();
	}
}
