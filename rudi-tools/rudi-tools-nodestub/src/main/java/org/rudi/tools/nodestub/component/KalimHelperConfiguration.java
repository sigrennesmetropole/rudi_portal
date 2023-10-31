/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.component;

import javax.net.ssl.SSLException;

import org.rudi.common.core.webclient.HttpClientHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

/**
 * @author FNI18300
 *
 */
@Configuration
@RequiredArgsConstructor
public class KalimHelperConfiguration {

	public static final String REGISTRATION_ID = "node_module";

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

	private final HttpClientHelper httpClientHelper;

	@Bean
	public ReactiveClientRegistrationRepository getRegistration() {
		ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(REGISTRATION_ID)
				.tokenUri(tokenUri).clientId(clientId).clientSecret(clientSecret)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).scope(scopes).build();
		return new InMemoryReactiveClientRegistrationRepository(clientRegistration);
	}

	@Bean
	public ReactiveOAuth2AuthorizedClientService clientService(ReactiveClientRegistrationRepository getRegistration) {
		return new InMemoryReactiveOAuth2AuthorizedClientService(getRegistration);
	}

	@Bean
	public ServerOAuth2AuthorizedClientRepository clientRepository(
			ReactiveOAuth2AuthorizedClientService clientService) {
		return new AuthenticatedPrincipalServerOAuth2AuthorizedClientRepository(clientService);
	}

	@Bean
	public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
			ReactiveClientRegistrationRepository getRegistration, ReactiveOAuth2AuthorizedClientService clientService,
			ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {
		ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder
				.builder().clientCredentials().build();

		AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
				getRegistration, clientService);

		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
		return authorizedClientManager;
	}

	@Bean(name = "node_oauth2_builder")
	public WebClient.Builder webClientBuilder(ReactiveOAuth2AuthorizedClientManager authorizedClientManager)
			throws SSLException {
		HttpClient httpClient = httpClientHelper.createReactorHttpClient(false, false, false);
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauthFilter.setDefaultClientRegistrationId(REGISTRATION_ID);
		return WebClient.builder().filter(oauthFilter)
				.defaultHeaders(header -> header.setBasicAuth(clientId, clientSecret))
				.clientConnector(new ReactorClientHttpConnector(httpClient));
	}

	@Bean(name = "node_oauth2")
	public WebClient webClient(@Qualifier("node_oauth2_builder") WebClient.Builder builder) {
		return builder.build();
	}

}
