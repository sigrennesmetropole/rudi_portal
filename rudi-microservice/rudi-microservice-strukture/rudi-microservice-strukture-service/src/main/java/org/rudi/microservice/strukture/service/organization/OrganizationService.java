package org.rudi.microservice.strukture.service.organization;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.acl.bean.User;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.core.bean.PasswordUpdate;
import org.rudi.microservice.strukture.service.exception.CannotRemoveLastAdministratorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service de gestion des organisations
 *
 * @author FNI18300
 *
 */
public interface OrganizationService {

	Organization createOrganization(Organization organization) throws AppServiceBadRequestException;

	Organization getOrganization(UUID uuid) throws AppServiceNotFoundException;

	User getOrganizationUserFromOrganizationUuid(UUID organizationUuid)
			throws AppServiceNotFoundException, AppServiceUnauthorizedException, AppServiceForbiddenException;

	void updateOrganization(Organization organization) throws AppServiceException;

	void deleteOrganization(UUID uuid) throws AppServiceNotFoundException;

	Page<Organization> searchOrganizations(OrganizationSearchCriteria searchCriteria, Pageable pageable);

	OrganizationMember addOrganizationMember(UUID organizationUuid, OrganizationMember organizationMember)
			throws Exception;

	List<OrganizationMember> getOrganizationMembers(UUID organizationUuid) throws AppServiceNotFoundException;

	void removeOrganizationMembers(UUID organizationUuid, UUID userUuid)
			throws AppServiceNotFoundException, CannotRemoveLastAdministratorException, AppServiceException;

	Page<OrganizationUserMember> searchOrganizationMembers(OrganizationMembersSearchCriteria searchCriteria, Pageable pageable) throws AppServiceException;

	Boolean isConnectedUserOrganizationAdministrator(UUID organizationUuid) throws AppServiceException;

	OrganizationMember updateOrganizationMember(UUID organizationUuid, UUID userUuid, OrganizationMember organizationMember) throws AppServiceException;

	void updateUserOrganizationPassword(UUID organizationUuid, PasswordUpdate passwordUpdate) throws AppServiceException;
}
