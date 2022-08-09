package org.rudi.microservice.strukture.service.organization.impl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserType;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.acl.helper.exceptions.CreateUserException;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.service.exception.CannotRemoveLastAdministratorException;
import org.rudi.microservice.strukture.service.mapper.OrganizationMapper;
import org.rudi.microservice.strukture.service.mapper.OrganizationMemberMapper;
import org.rudi.microservice.strukture.service.organization.OrganizationService;
import org.rudi.microservice.strukture.service.organization.impl.fields.CreateOrganizationFieldProcessor;
import org.rudi.microservice.strukture.service.organization.impl.fields.UpdateOrganizationFieldProcessor;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationCustomDao;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

	private final OrganizationDao organizationDao;
	private final OrganizationCustomDao organizationCustomDao;
	private final OrganizationMapper organizationMapper;
	private final Collection<CreateOrganizationFieldProcessor> createOrganizationFieldProcessors;
	private final Collection<UpdateOrganizationFieldProcessor> updateOrganizationFieldProcessors;

	private final OrganizationMemberMapper organizationMemberMapper;
	private final ProjektHelper projektHelper;
	private final ACLHelper aclHelper;
	private final UtilContextHelper utilContextHelper;
	@Value("${default.organization.roles:USER,ORGANIZATION}")
	private List<String> defaultOrganizationRoles;

	@Override
	public Organization getOrganization(UUID uuid) throws AppServiceNotFoundException {
		val entity = getOrganizationEntity(uuid);
		return organizationMapper.entityToDto(entity);
	}

	@Override
	public User getOrganizationUserFromOrganizationUuid(UUID organizationUuid) throws AppServiceNotFoundException, AppServiceUnauthorizedException, AppServiceForbiddenException {
		final var authenticatedUser = utilContextHelper.getAuthenticatedUser();
		final var authenticatedUserEntity = aclHelper.getUserByLogin(authenticatedUser.getLogin());
		if (authenticatedUserEntity == null) {
			throw new AppServiceUnauthorizedException(String.format("Cannot get organization %s user without authentication", organizationUuid));
		}

		final var organization = getOrganizationEntity(organizationUuid);
		if (!containsUserAsMember(organization, authenticatedUserEntity.getUuid())) {
			throw new AppServiceForbiddenException(String.format("Authenticated user %s is not member of organization %s", authenticatedUser.getLogin(), organizationUuid));
		}

		final var organizationUserLogin = getOrganizationUserLoginFromOrganizationUuid(organizationUuid);
		return aclHelper.getUserByLogin(organizationUserLogin);
	}

	private boolean containsUserAsMember(OrganizationEntity organization, UUID userUuid) {
		return organization.getMembers().stream().anyMatch(member -> member.getUserUuid().equals(userUuid));
	}

	private String getOrganizationUserLoginFromOrganizationUuid(UUID organizationUuid) {
		return organizationUuid.toString();
	}

	@Nonnull
	private OrganizationEntity getOrganizationEntity(UUID uuid) throws AppServiceNotFoundException {
		if (uuid == null) {
			throw new IllegalArgumentException("UUID required");
		}
		val entity = organizationDao.findByUuid(uuid);
		if (entity == null) {
			throw new AppServiceNotFoundException(OrganizationEntity.class, uuid);
		}
		return entity;
	}

	@Override
	@Transactional(readOnly = false)
	public Organization createOrganization(Organization organization) throws CreateUserException {
		val entity = organizationMapper.dtoToEntity(organization);
		for (final CreateOrganizationFieldProcessor processor : createOrganizationFieldProcessors) {
			processor.processBeforeCreate(entity);
		}
		val organizationCreated = organizationDao.save(entity);
		//Create user associated to organization
		User userCreated = aclHelper.createUser(this.createUser(organizationCreated.getUuid().toString(), organization.getPassword()));
		//Add user to organization
		OrganizationMember member = new OrganizationMember()
				.userUuid(userCreated.getUuid())
				.role(org.rudi.microservice.strukture.core.bean.OrganizationRole.EDITOR);
		organizationCreated.getMembers().add(organizationMemberMapper.dtoToEntity(member));
		return organizationMapper.entityToDto(entity);
	}

	private User createUser(String uuidOrganization, String password) {
		User user = new User();
		user.setLogin(uuidOrganization);
		user.setPassword(password);
		user.setType(UserType.ROBOT);
		List<Role> roles = aclHelper.searchRoles()
				.stream()
				.filter(element -> defaultOrganizationRoles.contains(element.getCode()))
				.collect(Collectors.toList());
		if(CollectionUtils.isNotEmpty(roles)) { //On part du principe que le rôle de chaque module a été créé via flyway
			user.setRoles(roles);
		}
		return user;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateOrganization(Organization organization) throws AppServiceException {
		val existingEntity = getOrganizationEntity(organization.getUuid());
		for (final UpdateOrganizationFieldProcessor processor : updateOrganizationFieldProcessors) {
			processor.processBeforeUpdate(organization, existingEntity);
		}
		organizationMapper.dtoToEntity(organization, existingEntity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteOrganization(UUID uuid) throws AppServiceNotFoundException {
		val entity = getOrganizationEntity(uuid);
		organizationDao.delete(entity);
	}

	@Override
	public Page<Organization> searchOrganizations(OrganizationSearchCriteria searchCriteria, Pageable pageable) {
		return organizationMapper.entitiesToDto(organizationCustomDao.searchOrganizations(searchCriteria, pageable), pageable);
	}


	@Override
	@Transactional // readOnly = false
	public OrganizationMember addOrganizationMember(UUID organizationUuid, OrganizationMember organizationMember) throws AppServiceNotFoundException {
		val organizationEntity = getOrganizationEntity(organizationUuid);
		val organizationMemberEntity = organizationMemberMapper.dtoToEntity(organizationMember);
		organizationEntity.getMembers().add(organizationMemberEntity);
		// Permet d'attendre l'ajout effectif du user à l'organisation avant d'envoyer la notif à projekt
		TransactionSynchronizationManager.registerSynchronization(
				new TransactionSynchronizationAdapter() {
					public void afterCommit() {
						//Notify projekt member has been added to update tasks candidates
						projektHelper.notifyUserHasBeenAdded(organizationUuid, organizationMember.getUserUuid());
					}
				}
		);
		return organizationMemberMapper.entityToDto(organizationMemberEntity);
	}

	@Override
	public List<OrganizationMember> getOrganizationMembers(UUID organizationUuid) throws AppServiceNotFoundException {
		val organizationEntity = getOrganizationEntity(organizationUuid);
		return organizationMemberMapper.entitiesToDto(organizationEntity.getMembers());
	}

	@Override
	@Transactional // readOnly = false
	public void removeOrganizationMembers(UUID organizationUuid, UUID userUuid) throws AppServiceNotFoundException, CannotRemoveLastAdministratorException {
		val organizationEntity = getOrganizationEntity(organizationUuid);

		final var anyAdministratorBeforeRemovingMember = organizationEntity.getMembers().stream()
				.filter(member -> member.getRole() == OrganizationRole.ADMINISTRATOR)
				.findAny();

		organizationEntity.getMembers().removeIf(member -> member.getUserUuid().equals(userUuid));

		final var anyAdministratorAfterRemovingMember = organizationEntity.getMembers().stream()
				.filter(member -> member.getRole() == OrganizationRole.ADMINISTRATOR)
				.findAny();

		if (anyAdministratorBeforeRemovingMember.isPresent() && anyAdministratorAfterRemovingMember.isEmpty()) {
			throw new CannotRemoveLastAdministratorException(userUuid, organizationUuid);
		}

		// Permet d'attendre l'ajout effectif du user à l'organisation avant d'envoyer la notif à projekt
		TransactionSynchronizationManager.registerSynchronization(
				new TransactionSynchronizationAdapter() {
					public void afterCommit() {
						//Notify projekt member has been deleted to update tasks candidates
						projektHelper.notifyUserHasBeenRemoved(organizationUuid, userUuid);
					}
				}
		);

	}

}
