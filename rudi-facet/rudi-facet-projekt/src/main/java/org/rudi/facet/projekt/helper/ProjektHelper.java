package org.rudi.facet.projekt.helper;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjektHelper {

	private final WebClient projektWebClient;
	private final ProjektProperties projektProperties;

	public void notifyUserHasBeenAdded(UUID organizationUuid, UUID userUuid) {
		projektWebClient.put()
				.uri(uriBuilder -> uriBuilder.path(buildProjektPostPutURL()).build(organizationUuid, userUuid))
				.retrieve()
				.bodyToMono(Void.class).block();
		return;
	}

	public void notifyUserHasBeenRemoved(UUID organizationUuid, UUID userUuid) {
		projektWebClient.delete()
				.uri(uriBuilder -> uriBuilder.path(buildProjektPostPutURL()).build(organizationUuid, userUuid))
				.retrieve()
				.bodyToMono(Void.class).block();
		return;
	}

	protected String buildProjektPostPutURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(getNoticationsEndpoint());
		return urlBuilder.toString();
	}

	private String getNoticationsEndpoint() {
		return projektProperties.getNotificationsPath();
	}

	/**
	 * On considère qu'un porteur de projet est autorisé à accéder à un JDD si au moins l'une des deux conditions est validée :
	 * <ul>
	 *     <li>Le JDD est ouvert</li>
	 *     <li>Le JDD est restreint mais au moins une demande d'accès a été acceptée (et est toujours valable) pour un des projets du porteur (projets en son nom ou au nom d'une de ses organisations)</li>
	 * </ul>
	 *
	 * @param ownerUuid UUID du porteur de projet
	 * @param globalId  UUID du jeu de données
	 * @return true si le porteur de projet est autorisé à accéder au jeu de données
	 */
	public boolean checkOwnerHasAccessToDataset(UUID ownerUuid, UUID globalId) {
		val ownerHasAccessToDataset = projektWebClient.get()
				.uri(uriBuilder -> uriBuilder
						.path(projektProperties.getCheckOwnerHasAccessToDatasetPath())
						.build(ownerUuid, globalId))
				.retrieve()
				.bodyToMono(Boolean.class)
				.block();
		return BooleanUtils.isTrue(ownerHasAccessToDataset);
	}

}
