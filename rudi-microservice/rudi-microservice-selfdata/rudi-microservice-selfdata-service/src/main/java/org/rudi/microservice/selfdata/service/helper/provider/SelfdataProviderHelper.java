package org.rudi.microservice.selfdata.service.helper.provider;

import java.util.List;
import java.util.UUID;

import org.apache.http.HttpHeaders;
import org.rudi.microservice.selfdata.core.bean.MatchingDescription;
import org.rudi.microservice.selfdata.core.bean.MatchingField;
import org.rudi.microservice.selfdata.service.exception.ProviderApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class SelfdataProviderHelper {

	@Autowired
	@Qualifier("rudi_selfdata_oauth2")
	private WebClient webClient;

	/**
	 * @param urlProvider
	 * @param datasetUuid
	 * @param userLogin
	 * @param matchingFields
	 * @return une description contenant un token si l'utilisateur est dans le JDD null sinon
	 * @throws ProviderApiException quand y a une erreur technique lévée du côté de l'API du provider
	 */
	public MatchingDescription sendMatchingDataForPairing(String urlProvider, UUID datasetUuid, String userLogin,
			List<MatchingField> matchingFields) throws ProviderApiException {
		if (urlProvider == null) {
			throw new IllegalArgumentException("urlProvider is null");
		}

		log.info("Send list of matching fields: {} ; size : {}", urlProvider,
				matchingFields != null ? matchingFields.size() : null);

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
		return urlProvider + "/matching/" + datasetUuid + "/" + login;
	}
}
