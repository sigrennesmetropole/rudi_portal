/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.service.helper.provider;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.rudi.facet.providers.helper.ProviderHelperConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.netty.http.client.HttpClient;

/**
 * @author FNI18300
 */
@Component
@RequiredArgsConstructor
public class KalimProviderWebclientConfiguration {

	private final ProviderHelperConfiguration providerHelperConfiguration;

	private final ObjectMapper objectMapper;

	@Bean(name = "rudi_kalim_oauth2_builder")
	public WebClient.Builder webClientBuilder(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {

		// Le client HTTP utilisé par WebClient
		HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE).wiretap(true);

		// Oauth2 authent pour le webclient
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(
				authorizedClientManager);
		oauthFilter.setDefaultClientRegistrationId(WebClientConfig.REGISTRATION_ID);

		// Définition du mapper à utiliser pour la (dé)sérialization lors des envois via Webclient
		ExchangeStrategies strategies = ExchangeStrategies.builder().codecs(clientDefaultCodecsConfigurer -> {
			clientDefaultCodecsConfigurer.defaultCodecs()
					.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
			clientDefaultCodecsConfigurer.defaultCodecs()
					.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
		}).build();

		return WebClient.builder()
				// Ajout de la gestion d'authent
				.filter(oauthFilter)
				// Ajout des headers pour l'authent
				.defaultHeaders(header -> header.setBasicAuth(providerHelperConfiguration.getClientId(),
						providerHelperConfiguration.getClientSecret()))
				// Ajout du client HTTP
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				// Mapping avec notre mapper custom
				.exchangeStrategies(strategies);
	}

	@Bean(name = "rudi_kalim_oauth2")
	public WebClient webClient(@Qualifier("rudi_kalim_oauth2_builder") WebClient.Builder builder) {
		return builder.build();
	}
}
