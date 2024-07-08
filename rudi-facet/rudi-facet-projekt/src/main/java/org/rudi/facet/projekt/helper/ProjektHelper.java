package org.rudi.facet.projekt.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.core.bean.PagedProjectList;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ProjectByOwner;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.core.bean.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.val;
import reactor.core.publisher.Mono;

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
		return Boolean.TRUE.equals(hasMonoAccessToDataset(uuidToCheck, globalId).block());
	}

	public Mono<Boolean> hasMonoAccessToDataset(UUID uuidToCheck, UUID globalId) {
		return projektWebClient.get().uri(uriBuilder -> uriBuilder.path(projektProperties.getHasAccessToDatasetPath())
				.build(uuidToCheck, globalId)).retrieve().bodyToMono(Boolean.class);
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
		ProjectSearchCriteria searchCriteria = new ProjectSearchCriteria().status(List.of(ProjectStatus.VALIDATED));
		Page<Project> projects = searchProjects(searchCriteria, Pageable.ofSize(1));
		return projects.getTotalElements();
	}

	public boolean hasProjectAccessToDataset(UUID projectUuid, UUID datasetUuid) {
		boolean result = false;
		ProjectSearchCriteria searchCriteria = new ProjectSearchCriteria().projectUuids(List.of(projectUuid))
				.datasetUuids(List.of(datasetUuid));
		Page<Project> projects = searchProjects(searchCriteria, Pageable.ofSize(1));
		if (!projects.isEmpty()) {
			Project project = projects.getContent().get(0);
			if (CollectionUtils.isNotEmpty(project.getLinkedDatasets())) {
				LinkedDataset linkedDataset = project.getLinkedDatasets().stream()
						.filter(item -> item.getDatasetUuid().equals(datasetUuid)).findFirst().orElse(null);
				if (linkedDataset != null && linkedDataset.getLinkedDatasetStatus() == LinkedDatasetStatus.VALIDATED) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param searchCriteria
	 * @param page
	 * @return
	 */
	public Page<Project> searchProjects(ProjectSearchCriteria searchCriteria, Pageable page) {
		PagedProjectList projects = projektWebClient.get().uri(uriBuilder -> uriBuilder
				.path(projektProperties.getSearchProjectsPath())
				.queryParamIfPresent("datasetUuids", Optional.ofNullable(searchCriteria.getDatasetUuids()))
				.queryParamIfPresent("linkedDatasetUuids", Optional.ofNullable(searchCriteria.getLinkedDatasetUuids()))
				.queryParamIfPresent("ownerUuids", Optional.ofNullable(searchCriteria.getOwnerUuids()))
				.queryParamIfPresent("projectUuids", Optional.ofNullable(searchCriteria.getProjectUuids()))
				.queryParamIfPresent("status", Optional.ofNullable(searchCriteria.getStatus()))
				.queryParamIfPresent("offset", Optional.ofNullable(page.getOffset()))
				.queryParamIfPresent("limit", Optional.ofNullable(page.getPageSize()))
				.queryParamIfPresent("order", Optional.ofNullable(convertSort(page.getSort()))).build()).retrieve()
				.bodyToMono(PagedProjectList.class).block();
		if (projects != null) {
			return new PageImpl<>(projects.getElements(), page, projects.getTotal());
		} else {
			return Page.empty();
		}
	}

	protected String convertSort(Sort sort) {
		if (sort == null || sort.isUnsorted()) {
			return null;
		}
		StringBuilder sortBuilder = new StringBuilder();
		sort.forEach(order -> {
			if (sortBuilder.length() > 0) {
				sortBuilder.append(',');
			}
			if (order.isDescending()) {
				sortBuilder.append('-');
			}
			sortBuilder.append(order.getProperty());
		});
		return sortBuilder.toString();
	}
}
