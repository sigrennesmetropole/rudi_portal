package org.rudi.facet.selfdata.helper;

import java.util.UUID;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SelfdataHelper {

	private final WebClient selfdataWebClient;

	private final SelfdataProperties selfdataProperties;

	/**
	 * On considère qu'un utilisateur est autorisé à accéder à un JDD Selfdata si sa demande d'accès sur ce JDD a été validé par l'animateur et le statut
	 * de sa demande est TraitéPrésent
	 *
	 * @param userUuid    UUID de l'utilisateur
	 * @param datasetUuid UUID du jeu de données
	 * @return true si l'utilisateur conecté à accès au jeu de données
	 */
	public boolean hasMatchingToDataset(UUID userUuid, UUID datasetUuid) {
		Boolean userHasAccessToDataset = hasMonoMatchingToDataset(userUuid, datasetUuid).block();
		return BooleanUtils.isTrue(userHasAccessToDataset);
	}

	public Mono<Boolean> hasMonoMatchingToDataset(UUID userUuid, UUID datasetUuid) {
		return selfdataWebClient.get().uri(uriBuilder -> uriBuilder
				.path(selfdataProperties.getHasMatchingToDatasetPath()).build(userUuid, datasetUuid)).retrieve()
				.bodyToMono(Boolean.class);
	}
}
