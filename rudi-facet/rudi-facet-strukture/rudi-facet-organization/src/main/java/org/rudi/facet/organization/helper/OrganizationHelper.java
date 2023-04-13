package org.rudi.facet.organization.helper;

import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.facet.organization.bean.Organization;
import org.rudi.facet.organization.bean.OrganizationMember;
import org.rudi.facet.organization.bean.PagedOrganizationList;
import org.rudi.facet.organization.helper.exceptions.AddUserToOrganizationException;
import org.rudi.facet.organization.helper.exceptions.CreateOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.facet.strukture.MonoUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrganizationHelper {

	private final OrganizationProperties organizationProperties;
	private final WebClient organizationWebClient;

	public boolean organizationContainsUser(UUID organizationUuid, UUID userUuid)
			throws GetOrganizationMembersException {
		if (organizationUuid == null) {
			throw new IllegalArgumentException("organizationUuid required");
		}
		if (userUuid == null) {
			throw new IllegalArgumentException("userUuid required");
		}
		final var members = getOrganizationMembers(organizationUuid);
		if (members == null) {
			return false;
		}
		return members.stream().anyMatch(member -> member.getUserUuid().equals(userUuid));
	}

	public OrganizationMember addMemberToOrganization(OrganizationMember member, UUID organizationUuid)
			throws AddUserToOrganizationException {
		var mono = organizationWebClient.post()
				.uri(uriBuilder -> uriBuilder.path(organizationProperties.getMembersPath()).build(organizationUuid))
				.body(Mono.just(member), OrganizationMember.class).retrieve().bodyToMono(OrganizationMember.class);
		return MonoUtils.blockOrThrow(mono, AddUserToOrganizationException.class);
	}

	public Collection<OrganizationMember> getOrganizationMembers(UUID organizationUuid)
			throws GetOrganizationMembersException {
		final var mono = organizationWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(organizationProperties.getMembersPath()).build(organizationUuid))
				.retrieve().bodyToMono(new ParameterizedTypeReference<List<OrganizationMember>>() {
				});
		return MonoUtils.blockOrThrow(mono, GetOrganizationMembersException.class);
	}

	/**
	 * @return l'organisation existante ou celle créée sinon
	 */
	public Organization createOrganizationIfNotExists(Organization organization)
			throws GetOrganizationException, CreateOrganizationException {
		final var existingOrganization = getOrganization(organization.getUuid());
		if (existingOrganization != null) {
			return existingOrganization;
		} else {
			return createOrganization(organization);
		}
	}

	/**
	 * @return null si l'organisation n'a pas été trouvée
	 */
	@Nullable
	public Organization getOrganization(UUID organizationUuid) throws GetOrganizationException {
		final var mono = organizationWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(organizationProperties.getOrganizationsPath())
						.queryParam("uuid", organizationUuid).build())
				.retrieve().bodyToMono(PagedOrganizationList.class);
		final var pagedOrganizationList = MonoUtils.blockOrThrow(mono, GetOrganizationException.class);
		if (pagedOrganizationList != null) {
			final var organizations = pagedOrganizationList.getElements();
			if (CollectionUtils.isNotEmpty(organizations)) {
				return organizations.get(0);
			}
		}
		return null;
	}

	@Nonnull
	public PagedOrganizationList searchOrganizations(Integer offset, Integer limit, String order) throws GetOrganizationException {
		final var mono = organizationWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(organizationProperties.getOrganizationsPath())
						.queryParam("offset", offset)
						.queryParam("limit", limit)
						.queryParam("order", order)
						.build())
				.retrieve().bodyToMono(PagedOrganizationList.class);
		final var pagedOrganizationList = MonoUtils.blockOrThrow(mono, GetOrganizationException.class);
		if (pagedOrganizationList != null) {
			return pagedOrganizationList;
		}
		return new PagedOrganizationList();
	}

	@Nonnull
	public List<Organization> getAllOrganizations() throws GetOrganizationException {
		List<Organization> organizations = new ArrayList<>();
		int offset = 0;
		long total;
		do {
			PagedOrganizationList organizationsPage = searchOrganizations(offset, 10, "name");
			total = organizationsPage.getTotal();
			if (CollectionUtils.isNotEmpty(organizationsPage.getElements())) {
				organizations.addAll(organizationsPage.getElements());
				offset += organizationsPage.getElements().size();
			}
		} while (organizations.size() < total);
		return organizations;
	}

	private Organization createOrganization(Organization organization) throws CreateOrganizationException {
		final var mono = organizationWebClient.post()
				.uri(uriBuilder -> uriBuilder.path(organizationProperties.getOrganizationsPath()).build())
				.body(Mono.just(organization), Organization.class).retrieve().bodyToMono(Organization.class);
		return MonoUtils.blockOrThrow(mono, CreateOrganizationException.class);
	}

	public List<UUID> getMyOrganizationsUuids(UUID userUuid) throws GetOrganizationException {
		final var mono = organizationWebClient.get()
				.uri(uriBuilder -> uriBuilder.path(organizationProperties.getOrganizationsPath())
						.queryParam("user_uuid", userUuid).build())
				.retrieve().bodyToMono(PagedOrganizationList.class);
		return extractUuidFromPageList(MonoUtils.blockOrThrow(mono, GetOrganizationException.class));
	}

	private List<UUID> extractUuidFromPageList(PagedOrganizationList page) {
		return page.getElements().stream().map(Organization::getUuid).collect(Collectors.toList());
	}
}
