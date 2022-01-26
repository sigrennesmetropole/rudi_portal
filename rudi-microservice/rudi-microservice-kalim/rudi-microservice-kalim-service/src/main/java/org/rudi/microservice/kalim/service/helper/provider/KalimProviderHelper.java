/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.service.helper.provider;

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHeaders;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.facet.providers.helper.ProviderHelperConfiguration;
import org.rudi.microservice.kalim.core.bean.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

/**
 * @author FNI18300
 */
@Component
public class KalimProviderHelper {

	@Autowired
	private ProviderHelper providerHelper;

	@Autowired
	private ProviderHelperConfiguration providerHelperConfiguration;

	@Autowired
	private UtilContextHelper utilContextHelper;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	@Qualifier("rudi_kalim_oauth2")
	private WebClient webClient;

	private static final Logger LOGGER = LoggerFactory.getLogger(KalimProviderHelper.class);

	/**
	 * Récupération de provider authentifié
	 *
	 * @return
	 */
	public Provider getAuthenticatedProvider() {
		AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
		return providerHelper.getProviderByNodeProviderUUID(UUID.fromString(authenticatedUser.getLogin()));
	}

	/**
	 * Récupération du noeud provider authentifié
	 *
	 * @return
	 */
	public NodeProvider getAuthenticatedNodeProvider() {
		AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();

		return providerHelper.getNodeProviderByUUID(UUID.fromString(authenticatedUser.getLogin()));
	}

	/**
	 * Recherche d'un noeud pour un provider donné
	 *
	 * @param provider
	 * @param providerNodeId
	 * @return
	 */
	public NodeProvider lookupNodeProvider(Provider provider, UUID providerNodeId) {
		NodeProvider result = null;
		if (provider != null && CollectionUtils.isNotEmpty(provider.getNodeProviders())) {
			Optional<NodeProvider> optional = provider.getNodeProviders().stream()
					.filter(n -> n.getUuid() == providerNodeId).findFirst();
			if (optional.isPresent()) {
				result = optional.get();
			}
		}
		return result;
	}

	/**
	 * Recupération de l'uuid du noeud provider authentifié
	 *
	 * @return
	 */
	public UUID getAuthenticatedNodeProviderUUID() {
		AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
		return UUID.fromString(authenticatedUser.getLogin());
	}

	/**
	 * Emission du rapport
	 *
	 * @param urlProvider
	 * @param report
	 */
	public void sendReport(String urlProvider, Report report) {

		if (urlProvider == null) {
			LOGGER.error("urlProvider is null");
			throw new IllegalArgumentException("urlProvider is null");
		}

		LOGGER.info("Send report: {} {}", urlProvider, report);

		webClient.put().uri(buildReportUrl(urlProvider, report.getResourceId().toString()))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body(Mono.just(report), Report.class).retrieve().bodyToMono(Report.class).block();
	}

	/**
	 * Construit l'url à appeler pour envoyer le rapport.
	 *
	 * @param urlProvider
	 * @param globalId    identifiant unique du metadata
	 * @return URL à appeler
	 */
	private String buildReportUrl(String urlProvider, String globalId) {

		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(urlProvider);
		urlBuilder.append("/resources/");
		urlBuilder.append(globalId);
		urlBuilder.append("/report");
		return urlBuilder.toString();

	}

	@Bean(name = "rudi_kalim_oauth2_builder")
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

	@Bean(name = "rudi_kalim_oauth2")
	public WebClient webClient(@Qualifier("rudi_kalim_oauth2_builder") WebClient.Builder builder) {
		return builder.build();
	}
}
