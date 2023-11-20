package org.rudi.microservice.strukture.service.organization.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.RoleCodes;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserType;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;
import org.rudi.microservice.strukture.core.bean.PasswordUpdate;
import org.rudi.microservice.strukture.service.exception.CannotRemoveLastAdministratorException;
import org.rudi.microservice.strukture.service.exception.UserIsNotOrganizationAdministratorException;
import org.rudi.microservice.strukture.service.helper.UserOrganizationEmailHelper;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationHelper;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersHelper;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersPartitionerHelper;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.reactive.function.client.UnknownHttpStatusCodeException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

	private static final Integer PASSWORD_LENGTH_ERROR_CODE = 444;
	private static final Integer MISSING_FIELD_PASSWORD_CHANGE = 445;
	private static final Integer PASSWORD_NOT_SECURE_ENOUGH = 446;
	private static final Integer INVALID_CREDENTIALS = 447;
	private static final Integer IDENTICAL_NEW_PASSWORD = 448;

	// Le nombre maximum d'UUIDs de membres qu'on veut exploiter pour croiser avec ACL afin de ne pas avoir
	// une requête HTTP vers ACL trop longue
	private static final int MAX_AMOUNT_OF_ORGANIZATION_MEMBERS = 50;

	private final OrganizationDao organizationDao;
	private final OrganizationCustomDao organizationCustomDao;
	private final OrganizationMapper organizationMapper;
	private final Collection<CreateOrganizationFieldProcessor> createOrganizationFieldProcessors;
	private final Collection<UpdateOrganizationFieldProcessor> updateOrganizationFieldProcessors;

	private final OrganizationMemberMapper organizationMemberMapper;
	private final ProjektHelper projektHelper;
	private final ACLHelper aclHelper;
	private final UtilContextHelper utilContextHelper;
	private final OrganizationMembersHelper organizationMembersHelper;
	private final OrganizationMembersPartitionerHelper organizationMembersPartitionerHelper;
	private final OrganizationHelper organizationHelper;
	private final UserOrganizationEmailHelper userOrganizationEmailHelper;

	@Value("${default.organization.roles:USER,ORGANIZATION}")
	private List<String> defaultOrganizationRoles;

	@Override
	public Organization getOrganization(UUID uuid) throws AppServiceNotFoundException {
		val entity = getOrganizationEntity(uuid);
		return organizationMapper.entityToDto(entity);
	}

	@Override
	public User getOrganizationUserFromOrganizationUuid(UUID organizationUuid)
			throws AppServiceNotFoundException, AppServiceUnauthorizedException, AppServiceForbiddenException {
		final var authenticatedUser = utilContextHelper.getAuthenticatedUser();
		final var authenticatedUserEntity = aclHelper.getUserByLogin(authenticatedUser.getLogin());
		if (authenticatedUserEntity == null) {
			throw new AppServiceUnauthorizedException(
					String.format("Cannot get organization %s user without authentication", organizationUuid));
		}

		final var organization = getOrganizationEntity(organizationUuid);

		if (!(containsUserAsMember(organization, authenticatedUserEntity.getUuid())
				|| utilContextHelper.hasAnyRoles(List.of(RoleCodes.ADMINISTRATOR, RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR)))) {
			throw new AppServiceForbiddenException(
					String.format("Authenticated user %s is not member of organization %s",
							authenticatedUserEntity.getLogin(), organizationUuid));
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
	@Transactional // (readOnly = false)
	public Organization createOrganization(Organization organization) throws AppServiceBadRequestException {
		val entity = organizationMapper.dtoToEntity(organization);
		for (final CreateOrganizationFieldProcessor processor : createOrganizationFieldProcessors) {
			processor.processBeforeCreate(entity);
		}
		val organizationCreated = organizationDao.save(entity);
		// Create user associated to organization
		User userCreated = aclHelper
				.createUser(this.createUser(organizationCreated.getUuid().toString(), organization.getPassword()));
		// Add user to organization
		OrganizationMember member = new OrganizationMember().userUuid(userCreated.getUuid())
				.role(org.rudi.microservice.strukture.core.bean.OrganizationRole.EDITOR).addedDate(LocalDateTime.now());
		organizationCreated.getMembers().add(organizationMemberMapper.dtoToEntity(member));
		return organizationMapper.entityToDto(entity);
	}

	private User createUser(String uuidOrganization, String password) {
		User user = new User();
		user.setLogin(uuidOrganization);
		user.setPassword(password);
		user.setType(UserType.ROBOT);
		List<Role> roles = aclHelper.searchRoles().stream()
				.filter(element -> defaultOrganizationRoles.contains(element.getCode())).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(roles)) { // On part du principe que le rôle de chaque module a été créé via flyway
			user.setRoles(roles);
		}
		return user;
	}

	@Override
	@Transactional // (readOnly = false)
	public void updateOrganization(Organization organization) throws AppServiceException {
		val existingEntity = getOrganizationEntity(organization.getUuid());
		for (final UpdateOrganizationFieldProcessor processor : updateOrganizationFieldProcessors) {
			processor.processBeforeUpdate(organization, existingEntity);
		}
		organizationMapper.dtoToEntity(organization, existingEntity);
	}

	@Override
	@Transactional // (readOnly = false)
	public void deleteOrganization(UUID uuid) throws AppServiceNotFoundException {
		val entity = getOrganizationEntity(uuid);
		organizationDao.delete(entity);
	}

	@Override
	public Page<Organization> searchOrganizations(OrganizationSearchCriteria searchCriteria, Pageable pageable) {
		return organizationMapper.entitiesToDto(organizationCustomDao.searchOrganizations(searchCriteria, pageable),
				pageable);
	}

	@Override
	@Transactional // readOnly = false
	public OrganizationMember addOrganizationMember(UUID organizationUuid, OrganizationMember organizationMember)
			throws Exception {
		// Verifier que l'utilisateur connecté a le droit d'agir
		if (!(utilContextHelper.hasAnyRoles(List.of(RoleCodes.ADMINISTRATOR, RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR, RoleCodes.MODULE_KALIM))
				|| isConnectedUserOrganizationAdministrator(organizationUuid))) {
			throw new UserIsNotOrganizationAdministratorException(String.format(
					"L'utilisateur connecté n'est pas autorisé à agir sur l'organisation %s", organizationUuid));
		}
		val organizationEntity = getOrganizationEntity(organizationUuid);
		// Verifier que le membre qu'on ajoute est user ACL
		val correspondingUser = organizationMembersHelper.getUserByLoginOrByUuid(organizationMember.getLogin(),
				organizationMember.getUserUuid());
		organizationMember.setUserUuid(correspondingUser.getUuid()); // Utile si le DTO ne contenait que le login
		val organizationMemberEntity = organizationMemberMapper.dtoToEntity(organizationMember);
		organizationMemberEntity.setAddedDate(LocalDateTime.now());
		organizationMembersHelper.checkUserIsNotMember(organizationEntity, organizationMemberEntity);
		organizationEntity.getMembers().add(organizationMemberEntity);
		// Permet d'attendre l'ajout effectif du user à l'organisation avant d'envoyer la notif à projekt
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				// Notify projekt member has been added to update tasks candidates
				projektHelper.notifyUserHasBeenAdded(organizationUuid, organizationMember.getUserUuid());
			}
		});
		return organizationMemberMapper.entityToDto(organizationMemberEntity);
	}

	@Override
	public List<OrganizationMember> getOrganizationMembers(UUID organizationUuid) throws AppServiceNotFoundException {
		val organizationEntity = getOrganizationEntity(organizationUuid);
		return organizationMemberMapper.entitiesToDto(organizationEntity.getMembers());
	}

	@Override
	@Transactional(rollbackFor = { CannotRemoveLastAdministratorException.class, RuntimeException.class })
	// readOnly = false
	public void removeOrganizationMembers(UUID organizationUuid, UUID userUuid) throws AppServiceException {
		// Vérification des droits pour l'utilisation de cette fonction
		if (!organizationMembersHelper.isConnectedUserOrganizationAdministrator(organizationUuid) && !utilContextHelper
				.hasAnyRoles(List.of(RoleCodes.ADMINISTRATOR, RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR))) {
			throw new AppServiceUnauthorizedException(
					"L'utilisateur connecté n'a pas le droit de manipuler cette organisation");
		}
		val organizationEntity = getOrganizationEntity(organizationUuid);

		final var anyAdministratorBeforeRemovingMember = organizationEntity.getMembers().stream()
				.filter(member -> member.getRole() == OrganizationRole.ADMINISTRATOR).findAny();

		organizationEntity.getMembers().removeIf(member -> member.getUserUuid().equals(userUuid));

		final var anyAdministratorAfterRemovingMember = organizationEntity.getMembers().stream()
				.filter(member -> member.getRole() == OrganizationRole.ADMINISTRATOR).findAny();

		if (anyAdministratorBeforeRemovingMember.isPresent() && anyAdministratorAfterRemovingMember.isEmpty()) {
			log.debug(String.format(
					"Il n'est pas possible de supprimer le dernier administrateur (userUuid = %s) de l'organisation %s",
					userUuid, organizationUuid));

			throw new CannotRemoveLastAdministratorException(
					"Il n'est pas possible de supprimer le dernier administrateur.");
		}

		// Permet d'attendre la suppression effective du user à l'organisation avant d'envoyer la notif à projekt
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCommit() {
				// Notify projekt member has been deleted to update tasks candidates
				projektHelper.notifyUserHasBeenRemoved(organizationUuid, userUuid);
			}
		});

	}

	@Override
	public Page<OrganizationUserMember> searchOrganizationMembers(OrganizationMembersSearchCriteria searchCriteria,
			Pageable pageable) throws AppServiceException {

		if (!organizationMembersHelper.isConnectedUserOrganizationAdministrator(searchCriteria.getOrganizationUuid())
				&& !utilContextHelper
				.hasAnyRoles(List.of(RoleCodes.ADMINISTRATOR, RoleCodes.MODULE_STRUKTURE_ADMINISTRATOR))) {
			throw new AppServiceUnauthorizedException(
					"L'utilisateur connecté n'a pas le droit de chercher des membres pour cette organisation");
		}

		List<Pageable> partitions = organizationMembersPartitionerHelper.getOrganizationMembersPartition(searchCriteria,
				MAX_AMOUNT_OF_ORGANIZATION_MEMBERS);

		List<OrganizationUserMember> members = new ArrayList<>();
		for (Pageable partition : partitions) {
			members.addAll(organizationMembersPartitionerHelper.partitionToEnrichedMembers(partition, searchCriteria));
		}

		return organizationMembersPartitionerHelper.extractPage(members, pageable);
	}

	@Override
	public Boolean isConnectedUserOrganizationAdministrator(UUID organizationUuid) throws AppServiceException {
		return organizationMembersHelper.isConnectedUserOrganizationAdministrator(organizationUuid);
	}

	@Override
	@Transactional
	public OrganizationMember updateOrganizationMember(UUID organizationUuid, UUID userUuid,
			OrganizationMember organizationMember) throws AppServiceException {

		// Vérifie que l'utilisateur connecté est bien administrateur de l'organisation
		if (!organizationMembersHelper.isConnectedUserOrganizationAdministrator(organizationUuid)) {
			throw new AppServiceUnauthorizedException(
					"L'utilisateur connecté n'a pas le droit de chercher des membres pour cette organisation");
		}

		// Vérifier que l'UUID de l'organisation passée correspond bien à une organisation connue, sinon throw une exception
		val existingOrganization = getOrganizationEntity(organizationUuid);

		// Vérifier que l'UUID du membre passé est bien lié à l'organisation passée en paramètre.
		var member = existingOrganization.getMembers().stream()
				.filter(orgaMember -> orgaMember.getUserUuid().equals(userUuid)).findFirst().orElse(null);
		if (member == null) {
			throw new AppServiceBadRequestException(
					"Les paramètres fournis ne permettent pas de réaliser une opération logique");
		}

		// Vérifier la cohérence entre les paramètres passés.
		if (!organizationUuid.equals(organizationMember.getUuid())
				|| !userUuid.equals(organizationMember.getUserUuid())) {
			throw new AppServiceBadRequestException(
					"Les paramètres fournis ne permettent pas de réaliser une opération logique");
		}

		// Vérifier qu'on modifie bien le role.
		if (member.getRole().equals(organizationMember.getRole())) {
			return organizationMemberMapper.entityToDto(member); // throw exception ?
		}

		// Vérifier que l'on ne modifie pas le dernier administrateur.
		if (isLastAdministrator(existingOrganization, userUuid)) {
			log.debug(String.format(
					"Il n'est pas possible de modifier le dernier administrateur (userUuid = %s) de l'organisation %s en éditeur",
					userUuid, organizationUuid));
			throw new CannotRemoveLastAdministratorException(
					"Il n'est pas possible de supprimer le dernier administrateur.");
		}

		organizationMemberMapper.dtoToEntity(organizationMember, member);

		// Pour Jules : Un mapper.dtoToEntity(dto, entity) ne fait pas de modification de la BD, il FAUT un dao.save(entity)
		organizationDao.save(existingOrganization);

		return organizationMemberMapper.entityToDto(member);
	}

	private boolean isLastAdministrator(OrganizationEntity organization, UUID userUuid) {
		val adminMembers = organization.getMembers().stream()
				.filter(orgaMember -> OrganizationRole.ADMINISTRATOR.equals(orgaMember.getRole()))
				.collect(Collectors.toList());
		return adminMembers.size() == 1 && adminMembers.get(0).getUserUuid().equals(userUuid);
	}

	@Override
	public void updateUserOrganizationPassword(UUID organizationUuid, PasswordUpdate passwordUpdate)
			throws AppServiceException {

		// vérification que l'utilisateur connecté a bien le droit de faire ça
		if (!organizationMembersHelper.isConnectedUserOrganizationAdministrator(organizationUuid)) {
			throw new AppServiceForbiddenException(
					"L'utilisateur connecté n'a pas le droit de modifier le mot de passe.");
		}

		// Récupération de l'organisation
		OrganizationEntity organization = organizationDao.findByUuid(organizationUuid);

		// Modification du mot de passe de l'utilisateur robot (son login = UUID de l'organisation)
		try {
			aclHelper.updateUserPassword(organization.getUuid().toString(), passwordUpdate.getOldPassword(),
					passwordUpdate.getNewPassword());
		} catch (UnknownHttpStatusCodeException e) {
			throw handleAclCustomError(e);
		} catch (WebClientResponseException e) {
			throw handleAclError(e);
		}

		// Récupération de tous les administrateurs de l'organisation pour leur envoyer un mail
		List<User> userAdministrators = organizationHelper.searchUserAdministrators(organization.getUuid());
		userOrganizationEmailHelper.sendUserOrganizationUpdatePasswordConfirmation(organization, userAdministrators,
				Locale.FRENCH);
	}

	private AppServiceException handleAclCustomError(UnknownHttpStatusCodeException e) {
		if (e.getRawStatusCode() < 400 || e.getRawStatusCode() >= 500) {
			return new AppServiceException("Un code d'erreur non géré dans strukture a été lancé par ACL lors de "
					+ "la modification du mot de passe de l'utilisateur d'organisation");
		}

		if (e.getRawStatusCode() == PASSWORD_LENGTH_ERROR_CODE) {
			return new AppServiceBadRequestException(
					"Le nouveau mot de passe ne respecte pas le nombre de caractères requis.");
		} else if (e.getRawStatusCode() == MISSING_FIELD_PASSWORD_CHANGE) {
			return new AppServiceBadRequestException("Le nouveau mot de passe est absent.");
		} else if (e.getRawStatusCode() == PASSWORD_NOT_SECURE_ENOUGH) {
			return new AppServiceBadRequestException("Le nouveau mot de passe n'est pas assez sécurisé.");
		} else if (e.getRawStatusCode() == INVALID_CREDENTIALS) {
			return new AppServiceBadRequestException("L'ancien mot de passe saisi est incorrect.");
		} else if (e.getRawStatusCode() == IDENTICAL_NEW_PASSWORD) {
			return new AppServiceBadRequestException(
					"Le nouveau mot de passe ne peut pas être identique à " + "l'ancien mot de passe.");
		}

		return new AppServiceException("Une erreur inconnue s'est produite "
				+ "lors de la modification du mot de passe de l'utilisateur d'organisation dans ACL", e);
	}

	private AppServiceException handleAclError(WebClientResponseException e) {
		if (e.getRawStatusCode() == 401) {
			return new AppServiceUnauthorizedException("Veuillez vous reconnecter afin de poursuivre la procédure.");
		} else if (e.getRawStatusCode() == 403) {
			return new AppServiceForbiddenException("Vous n'avez plus les droits pour effectuer cette action.");
		}

		return new AppServiceException("Une erreur s'est produite "
				+ "lors de la modification du mot de passe de l'utilisateur d'organisation dans ACL", e);
	}
}
