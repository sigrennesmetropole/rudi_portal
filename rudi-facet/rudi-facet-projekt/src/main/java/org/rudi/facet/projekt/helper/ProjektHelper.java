package org.rudi.facet.projekt.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.rudi.microservice.projekt.core.bean.PagedProjectList;
import org.rudi.microservice.projekt.core.bean.ProjectByOwner;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.core.bean.ProjectStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class ProjektHelper {

	private final WebClient projektWebClient;
	private final ProjektProperties projektProperties;

	public void notifyUserHasBeenAdded(UUID organizationUuid, UUID userUuid) {
		projektWebClient.put()
				.uri(uriBuilder -> uriBuilder.path(buildProjektPostPutURL()).build(organizationUuid, userUuid))
				.retrieve().bodyToMono(Void.class).block();
	}

	public void notifyUserHasBeenRemoved(UUID organizationUuid, UUID userUuid) {
		projektWebClient.delete()
				.uri(uriBuilder -> uriBuilder.path(buildProjektPostPutURL()).build(organizationUuid, userUuid))
				.retrieve().bodyToMono(Void.class).block();
	}

	protected String buildProjektPostPutURL() {
		return getNoticationsEndpoint();
	}

	private String getNoticationsEndpoint() {
		return projektProperties.getNotificationsPath();
	}

	/**
	 * On considère qu'un porteur de projet est autorisé à accéder à un JDD si au moins l'une des deux conditions est validée :
	 * <ul>
	 * <li>Le JDD est ouvert</li>
	 * <li>Le JDD est restreint mais au moins une demande d'accès a été acceptée (et est toujours valable)</li>
	 * </ul>
	 *
	 * @param uuidToCheck UUID dont on veut tester l'autorisation d'accès au dataset
	 * @param globalId    UUID du jeu de données
	 * @return true si l'uuid to check est autorisé à accéder au jeu de données, null sinon
	 */
	public boolean hasAccessToDataset(UUID uuidToCheck, UUID globalId) {
		return Boolean.TRUE.equals(
				projektWebClient.get().uri(uriBuilder -> uriBuilder.path(projektProperties.getHasAccessToDatasetPath())
						.build(uuidToCheck, globalId)).retrieve().bodyToMono(Boolean.class).block());
	}

	/**
	 * @param linkedDatasetUuid uuid de la demande
	 * @return l'uuid du projectOwner ayant fait la demande
	 */
	public UUID getLinkedDatasetOwner(UUID linkedDatasetUuid) {
		return projektWebClient.get().uri(
				uriBuilder -> uriBuilder.path(projektProperties.getLinkedDatasetOwnerPath()).build(linkedDatasetUuid))
				.retrieve().bodyToMono(UUID.class).block();
	}

	public List<ProjectByOwner> getNumberOfProjectsPerOwners(List<UUID> ownerUuids) {
		ProjectSearchCriteria criteria = new ProjectSearchCriteria().ownerUuids(ownerUuids);
		val projectByOwners = projektWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(projektProperties.getGetNumberOfProjectsPerOwnersPath())
						.queryParam("criteria", criteria).build())
				.retrieve().bodyToMono(ProjectByOwner[].class).block();

		// Null safety projectByOwners
		return Optional.ofNullable(projectByOwners).map(Arrays::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	public Long getNumberOfValidatedProjects() {
		PagedProjectList validatedProjectList = projektWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(projektProperties.getGetProjectsPath())
						.queryParam("status", Arrays.asList(ProjectStatus.VALIDATED)).queryParam("offset", 0)
						.queryParam("limit", 0).build())
				.retrieve().bodyToMono(PagedProjectList.class).block();

		return validatedProjectList.getTotal();
	}
}
