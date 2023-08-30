/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.service.helper.provider;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHeaders;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.facet.providers.bean.Provider;
import org.rudi.facet.providers.helper.ProviderHelper;
import org.rudi.microservice.kalim.core.bean.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author FNI18300
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KalimProviderHelper {

	private final ProviderHelper providerHelper;

	private final UtilContextHelper utilContextHelper;

	@Autowired
	@Qualifier("rudi_kalim_oauth2")
	private WebClient webClient;

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
			log.error("urlProvider is null");
			throw new IllegalArgumentException("urlProvider is null");
		}

		log.info("Send report: {} {}", urlProvider, report);

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
}
