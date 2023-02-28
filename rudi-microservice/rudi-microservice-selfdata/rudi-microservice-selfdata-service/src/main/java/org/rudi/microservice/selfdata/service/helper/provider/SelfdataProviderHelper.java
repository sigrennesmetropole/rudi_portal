package org.rudi.microservice.selfdata.service.helper.provider;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.rudi.facet.providers.helper.ProviderHelperConfiguration;
import org.rudi.microservice.selfdata.core.bean.MatchingDescription;
import org.rudi.microservice.selfdata.core.bean.MatchingField;
import org.rudi.microservice.selfdata.service.exception.ProviderApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SelfdataProviderHelper {
	private final ObjectMapper objectMapper;
	private final ProviderHelperConfiguration providerHelperConfiguration;

	@Autowired
	@Qualifier("rudi_selfdata_oauth2")
	private WebClient webClient;

	private static final Logger LOGGER = LoggerFactory.getLogger(SelfdataProviderHelper.class);

	/**
	 * @param urlProvider
	 * @param datasetUuid
	 * @param userLogin
	 * @param matchingFields
	 * @return une description contenant un token si l'utilisateur est dans le JDD null sinon
	 * @throws ProviderApiException quand y a une erreur technique lévée du côté de l'API du provider
	 */
	public MatchingDescription sendMatchingDataForPairing(String urlProvider, UUID datasetUuid, String userLogin, List<MatchingField> matchingFields) throws ProviderApiException {
		if (urlProvider == null) {
			throw new IllegalArgumentException("urlProvider is null");
		}

		LOGGER.info("Send list of matching fields: {} {}", urlProvider, matchingFields);

		try {
			return webClient.post().uri(buildCreateMatchingTokenUrl(urlProvider, datasetUuid, userLogin))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.body(Mono.just(matchingFields), new ParameterizedTypeReference<List<MatchingField>>() { // On ignore la règle sonar qui pour le type paramétré sinon échec compilation boost (dû à des versions de JDK sûrement)
					}).retrieve().bodyToMono(MatchingDescription.class).block();
		} catch (WebClientResponseException.NotFound exception) {
			// Correspond au cas TraitéeDonnéesAbsentes dans un JDD
			return null;
		} catch (Exception exception) {
			throw new ProviderApiException("Une erreur est survenue du côté de l'API du provider", exception);
		}
	}

	private String buildCreateMatchingTokenUrl(String urlProvider, UUID datasetUuid, String login) {
		return urlProvider +
				"/matching/" +
				datasetUuid +
				"/" +
				login;
	}

	@Bean(name = "rudi_selfdata_oauth2_builder")
	public WebClient.Builder webClientBuilder(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {

		// Le client HTTP utilisé par WebClient
		HttpClient httpClient = HttpClient.create().wiretap(true);

		// Oauth2 authent pour le webclient
		ServerOAuth2AuthorizedClientExchangeFilterFunction oauthFilter =
				new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
		oauthFilter.setDefaultClientRegistrationId(WebClientConfig.REGISTRATION_ID);

		// Définition du mapper à utiliser pour la (dé)sérialization lors des envois via Webclient
		ExchangeStrategies strategies = ExchangeStrategies
				.builder()
				.codecs(clientDefaultCodecsConfigurer -> {
					clientDefaultCodecsConfigurer.defaultCodecs()
							.jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
					clientDefaultCodecsConfigurer.defaultCodecs()
							.jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
				}).build();


		return WebClient.builder()
				// Ajout de la gestion d'authent
				.filter(oauthFilter)
				// Ajout des headers pour l'authent
				.defaultHeaders(header -> header.setBasicAuth(
								providerHelperConfiguration.getClientId(),
								providerHelperConfiguration.getClientSecret()
						)
				)
				// Ajout du client HTTP
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				// Mapping avec notre mapper custom
				.exchangeStrategies(strategies);
	}

	@Bean(name = "rudi_selfdata_oauth2")
	public WebClient webClient(@Qualifier("rudi_selfdata_oauth2_builder") WebClient.Builder builder) {
		return builder.build();
	}
}
