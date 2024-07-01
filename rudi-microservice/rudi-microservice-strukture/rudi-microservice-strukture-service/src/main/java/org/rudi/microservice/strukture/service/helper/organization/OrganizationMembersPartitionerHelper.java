package org.rudi.microservice.strukture.service.helper.organization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.facet.acl.bean.User;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;
import org.rudi.microservice.strukture.service.helper.organization.comparator.OrganizationMemberAddedDateComparator;
import org.rudi.microservice.strukture.service.helper.organization.comparator.OrganizationMemberFirstNameComparator;
import org.rudi.microservice.strukture.service.helper.organization.comparator.OrganizationMemberLastConnexionComparator;
import org.rudi.microservice.strukture.service.helper.organization.comparator.OrganizationMemberLastNameComparator;
import org.rudi.microservice.strukture.service.helper.organization.comparator.OrganizationMemberLoginComparator;
import org.rudi.microservice.strukture.service.helper.organization.comparator.OrganizationMemberRoleComparator;
import org.rudi.microservice.strukture.storage.dao.organizationmember.OrganizationMemberCustomDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationMembersPartitionerHelper {

	static Map<OrganizationMemberSort, Comparator<OrganizationUserMember>> comparators = new EnumMap<>(
			OrganizationMemberSort.class);

	private final OrganizationMembersHelper organizationMembersHelper;
	private final OrganizationMemberCustomDao organizationMemberCustomDao;

	@PostConstruct
	void setMap() {
		comparators.put(OrganizationMemberSort.LOGIN, new OrganizationMemberLoginComparator());
		comparators.put(OrganizationMemberSort.FIRSTNAME, new OrganizationMemberFirstNameComparator());
		comparators.put(OrganizationMemberSort.LASTNAME, new OrganizationMemberLastNameComparator());
		comparators.put(OrganizationMemberSort.LAST_CONNEXION, new OrganizationMemberLastConnexionComparator());
		comparators.put(OrganizationMemberSort.ADDED_DATE, new OrganizationMemberAddedDateComparator());
		comparators.put(OrganizationMemberSort.ROLE, new OrganizationMemberRoleComparator());
	}

	/**
	 * Récupère un ensemble de partitions d'utilisateurs d'organisation selon une taille donnée
	 *
	 * @param searchCriteria les critères pour chercher la bonne organisation
	 * @param partitionSize  la taille d'une partition
	 * @return un ensemble de partitions
	 */
	public List<Pageable> getOrganizationMembersPartition(OrganizationMembersSearchCriteria searchCriteria,
			int partitionSize) {

		if (searchCriteria == null) {
			return Collections.emptyList();
		}

		Page<OrganizationMemberEntity> page = organizationMemberCustomDao.searchOrganizationMembers(searchCriteria,
				PageRequest.of(0, 1));

		long totalElements = page.getTotalElements();
		long numberOfPartitions;
		if (totalElements < partitionSize) {
			numberOfPartitions = 1;
		} else {
			double partitions = (double)totalElements / partitionSize;
			numberOfPartitions = (long) Math.ceil(partitions);
		}

		List<Pageable> partitions = new ArrayList<>();
		for (int i = 0; i < numberOfPartitions; i++) {
			partitions.add(PageRequest.of(i, partitionSize));
		}

		return partitions;
	}

	/**
	 * Récupère une liste de membres enrichis à l'aide d'une partition et de critères de filtrages les éléments renvoyés sont les membres correspondant au
	 * filtrage et ayant un user ACL correspondant
	 *
	 * @param partition      la partition des membres à aller convertir
	 * @param searchCriteria les critères de filtres
	 * @return une liste de membres enrichis
	 */
	public List<OrganizationUserMember> partitionToEnrichedMembers(Pageable partition,
			OrganizationMembersSearchCriteria searchCriteria) {

		Page<OrganizationMemberEntity> basicMembers = organizationMemberCustomDao.searchOrganizationMembers(searchCriteria, partition);
		List<User> correspondingUsers = organizationMembersHelper.searchCorrespondingUsers(basicMembers.getContent(),
				searchCriteria);

		Map<Boolean, List<OrganizationMemberEntity>> partitionedMembers = organizationMembersHelper
				.splitMembersHavingCorrespondingUsers(basicMembers.getContent(), correspondingUsers);

		List<OrganizationMemberEntity> wantedMembers = partitionedMembers.get(true);
		return organizationMembersHelper.mergeMembersAndUsers(wantedMembers, correspondingUsers);
	}

	/**
	 * Extrait une "Page" à partir d'une liste de membres selon des critères de pagination
	 *
	 * @param members  les membres totaux de la recherche
	 * @param pageable critères de pagination
	 * @return une page de membres
	 */
	public Page<OrganizationUserMember> extractPage(List<OrganizationUserMember> members, Pageable pageable)
			throws AppServiceBadRequestException {

		int offset = (int) pageable.getOffset();
		if (offset > members.size()) {
			return Page.empty();
		}

		int size = (int) pageable.getOffset() + pageable.getPageSize();
		if (size > members.size()) {
			size = members.size();
		}

		Comparator<OrganizationUserMember> comparator = getSortingComparator(pageable);
		if (comparator != null) {
			members.sort(comparator);
		}

		List<OrganizationUserMember> content = members.subList(offset, size);
		return new PageImpl<>(content, pageable, members.size());
	}

	/**
	 * Récupération d'un comparateur Java à appliquer à une collection à partir du pageable
	 *
	 * @param pageable le pageable contenant les informations pour faire le tri
	 * @return un comparateur pour faire le tri
	 * @throws AppServiceBadRequestException erreur si un critère est invalide
	 */
	private Comparator<OrganizationUserMember> getSortingComparator(Pageable pageable)
			throws AppServiceBadRequestException {

		List<Sort.Order> orders = pageable.getSort().get().collect(Collectors.toList());
		if (CollectionUtils.isEmpty(orders)) {
			return null;
		}

		List<Sort.Order> badOrders = new ArrayList<>();
		Map<Sort.Order, OrganizationMemberSort> ordersMapped = new HashMap<>();
		for (Sort.Order order : orders) {
			OrganizationMemberSort sort = OrganizationMemberSort.from(order.getProperty());
			if (sort != null) {
				ordersMapped.put(order, sort);
			} else {
				badOrders.add(order);
			}
		}

		if (CollectionUtils.isNotEmpty(badOrders)) {
			List<String> unknownOrders = badOrders.stream().map(Sort.Order::getProperty).collect(Collectors.toList());
			throw new AppServiceBadRequestException("Critère(s) de tri non reconnu(s) : " + unknownOrders);
		}

		Comparator<OrganizationUserMember> finalComparator = null;
		for (Map.Entry<Sort.Order, OrganizationMemberSort> entry : ordersMapped.entrySet()) {
			Comparator<OrganizationUserMember> comparator = comparators.get(entry.getValue());
			Sort.Order order = entry.getKey();
			if (order.isDescending()) {
				comparator = comparator.reversed();
			}

			if (finalComparator == null && comparator != null) {
				finalComparator = comparator;
			} else if (comparator != null) {
				finalComparator = finalComparator.thenComparing(comparator);
			}
		}

		return finalComparator;
	}
}
