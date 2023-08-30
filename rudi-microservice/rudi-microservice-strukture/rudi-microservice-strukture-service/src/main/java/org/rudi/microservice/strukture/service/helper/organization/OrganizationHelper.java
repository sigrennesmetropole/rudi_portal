package org.rudi.microservice.strukture.service.helper.organization;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.facet.acl.bean.User;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@Transactional
@RequiredArgsConstructor
public class OrganizationHelper {

	private static final Integer MAX_AMOUNT_OF_USERS_RETRIEVED = 50;

	private final OrganizationDao organizationDao;
	private final OrganizationMembersHelper organizationMembersHelper;

	/**
	 * Recherche des utilisateurs ACL qui sont administrateurs de l'organisation passée en paramètre
	 *
	 * @param organizationUuid UUID de l'organisation concernée
	 * @return liste des administrateurs au format User de ACL
	 */
	public List<User> searchUserAdministrators(UUID organizationUuid) throws AppServiceException {

		OrganizationEntity organization = organizationDao.findByUuid(organizationUuid);
		if (organization == null) {
			throw new AppServiceNotFoundException(OrganizationEntity.class, organizationUuid);
		}

		List<OrganizationMemberEntity> members = organization.getMembers()
				.stream().filter(member -> OrganizationRole.ADMINISTRATOR.equals(member.getRole()))
				.collect(Collectors.toList());

		List<List<OrganizationMemberEntity>> membersPartitioned = ListUtils
				.partition(members, MAX_AMOUNT_OF_USERS_RETRIEVED);

		return membersPartitioned.stream()
				.map(partition -> organizationMembersHelper.searchCorrespondingUsers(partition,
						new OrganizationMembersSearchCriteria()))
				.reduce(new ArrayList<>(), (total, element) -> {
					total.addAll(element);
					return total;
				});
	}
}
