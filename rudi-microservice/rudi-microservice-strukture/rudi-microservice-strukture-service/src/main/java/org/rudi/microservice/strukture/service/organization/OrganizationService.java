package org.rudi.microservice.strukture.service.organization;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.exceptions.CreateUserException;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.service.exception.CannotRemoveLastAdministratorException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {

	Organization createOrganization(Organization organization) throws AppServiceBadRequestException;

	Organization getOrganization(UUID uuid) throws AppServiceNotFoundException;

	User getOrganizationUserFromOrganizationUuid(UUID organizationUuid) throws AppServiceNotFoundException, AppServiceUnauthorizedException, AppServiceForbiddenException;

	void updateOrganization(Organization organization) throws AppServiceException;

	void deleteOrganization(UUID uuid) throws AppServiceNotFoundException;

	Page<Organization> searchOrganizations(OrganizationSearchCriteria searchCriteria, Pageable pageable);

	OrganizationMember addOrganizationMember(UUID organizationUuid, OrganizationMember organizationMember) throws AppServiceNotFoundException;

	List<OrganizationMember> getOrganizationMembers(UUID organizationUuid) throws AppServiceNotFoundException;

	void removeOrganizationMembers(UUID organizationUuid, UUID userUuid) throws AppServiceNotFoundException, CannotRemoveLastAdministratorException;
}
