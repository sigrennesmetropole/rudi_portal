package org.rudi.microservice.strukture.service.helper.organization;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceExceptionsStatus;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;
import org.rudi.microservice.strukture.service.exception.UserIsAlreadyOrganizationMemberException;
import org.rudi.microservice.strukture.service.exception.UserNotFoundException;
import org.rudi.microservice.strukture.service.mapper.OrganizationMemberMapper;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrganizationMembersHelper {

	private final ACLHelper aclHelper;
	private final OrganizationDao organizationDao;
	private final OrganizationMemberMapper organizationMemberMapper;
	private final UtilContextHelper utilContextHelper;

	/**
	 * Vérifie si l'utilisateur connecté est administrateur de l'organisation founie
	 *
	 * @param organizationUuid l'organisation testée
	 * @return si le droit ou non
	 * @throws AppServiceException erreur non autorisé, ou erreur technique
	 */
	public boolean isConnectedUserOrganizationAdministrator(UUID organizationUuid) throws AppServiceException {

		OrganizationEntity organization = organizationDao.findByUuid(organizationUuid);
		if (organization == null) {
			throw new AppServiceNotFoundException(OrganizationEntity.class, organizationUuid);
		}

		AuthenticatedUser authenticatedUser = utilContextHelper.getAuthenticatedUser();
		if (authenticatedUser == null) {
			throw new AppServiceUnauthorizedException("Aucun utilisateur connecté");
		}

		User user = aclHelper.getUserByLogin(authenticatedUser.getLogin());
		if (user == null) {
			throw new AppServiceUnauthorizedException(
					"Aucun utilisateur correspondant à l'utilisateur connecté dans ACL");
		}

		return organization.getMembers().stream().anyMatch(member -> member.getUserUuid().equals(user.getUuid())
				&& member.getRole().equals(OrganizationRole.ADMINISTRATOR));
	}

	/**
	 * Merge les membres d'organisations avec leurs informations d'utilisateur
	 *
	 * @param members les membres d'une organisation
	 * @param users   les utilisateurs correspondants
	 * @return une liste de membres avec info utilisateurs
	 */
	public List<OrganizationUserMember> mergeMembersAndUsers(List<OrganizationMemberEntity> members, List<User> users) {

		if (CollectionUtils.isEmpty(members)) {
			return Collections.emptyList();
		}

		Map<UUID, User> usersByUuid = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));
		return members.stream().map(member -> buildUserMember(member, usersByUuid.get(member.getUserUuid())))
				.collect(Collectors.toList());
	}

	/**
	 * Partitionne les membres de l'organisation fournis entre ceux qui ont un utilisateur ACL correspondant et ceux n'en ayant pas
	 *
	 * @param members les membres de l'organisation
	 * @param users   les utilisateurs de ACL sensés correspondres
	 * @return une partition entre les membres avec utilisateurs et les membres sans
	 */
	public Map<Boolean, List<OrganizationMemberEntity>> splitMembersHavingCorrespondingUsers(
			List<OrganizationMemberEntity> members, List<User> users) {

		Map<UUID, User> mapUsers = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

		return members.stream()
				.collect(Collectors.partitioningBy(member -> mapUsers.get(member.getUserUuid()) != null));
	}

	/**
	 * Recherche des utilisateurs dans ACLs correspondant aux membres fournis avec un filtre custom
	 *
	 * @param members        les membres de l'organisation
	 * @param searchCriteria critères de recherche initial
	 * @return une liste d'utilisateurs ACL
	 */
	public List<User> searchCorrespondingUsers(List<OrganizationMemberEntity> members,
			OrganizationMembersSearchCriteria searchCriteria) {

		List<UUID> memberUuids = members.stream().map(OrganizationMemberEntity::getUserUuid)
				.collect(Collectors.toList());

		String searchText = "";
		String userType = "";
		if (searchCriteria != null) {
			searchText = searchCriteria.getSearchText() != null ? searchCriteria.getSearchText() : "";
			userType = searchCriteria.getType() != null ? searchCriteria.getType().toString() : "";
		}

		return aclHelper.searchUsersWithCriteria(memberUuids, searchText, userType);
	}

	/**
	 * @param login    de l'utilisateur
	 * @param userUuid de l'utilsateur
	 * @return le user correspondant soit au login soit à l'uuid, si les deux sont passés, l'uuid prime
	 * @throws AppServiceException MissingParameterException ou UserNotFoundException
	 */
	public User getUserByLoginOrByUuid(String login, UUID userUuid) throws AppServiceException {
		if (StringUtils.isEmpty(login) && userUuid == null) {
			throw new MissingParameterException("Le login ou l'uuid de l'utilisateur est obligatoire");
		}
		User userByUUID = null;
		User userByLogin = null;
		if (userUuid != null) {
			userByUUID = aclHelper.getUserByUUID(userUuid);
		}
		if (login != null) {
			userByLogin = aclHelper.getUserByLogin(login);
		}
		// Si les 2 infos sont passées, il faut de la cohérence entre les 2
		if (userUuid != null && login != null) {
			if (userByUUID != null && userByLogin != null && userByLogin.getUuid().equals(userByUUID.getUuid())) {
				return userByLogin;
			} else {
				throw new UserNotFoundException(
						"Une erreur est survenue. Les informations d'identifications ne sont pas correctes ou cohérentes entre elles",
						AppServiceExceptionsStatus.NOT_FOUND);
			}
		}
		// Si seulement le userUuid a été passé
		if (userUuid != null) {
			if (userByUUID == null) {
				throw new UserNotFoundException("L'UUID renseignée n'est pas rattachée à un utilisateur RUDI.",
						AppServiceExceptionsStatus.NOT_FOUND);
			}
			return userByUUID;
		}
		// Si seulement le login a été passé
		if (userByLogin == null) {
			throw new UserNotFoundException("L'adresse e-mail renseignée n'est pas rattachée à un utilisateur RUDI.",
					AppServiceExceptionsStatus.NOT_FOUND);
		}
		return userByLogin;
	}

	/**
	 * Throw une exception si l'utilisateur est déjà membre ce qui arrête donc le processus d'ajout
	 *
	 * @param organizationEntity       organisation en cours de traitement
	 * @param organizationMemberEntity membre à ajouter
	 * @throws UserIsAlreadyOrganizationMemberException exception levée si utilisateur déjà membre
	 */
	public void checkUserIsNotMember(OrganizationEntity organizationEntity,
			OrganizationMemberEntity organizationMemberEntity) throws UserIsAlreadyOrganizationMemberException {
		val isMember = organizationEntity.getMembers().stream()
				.anyMatch(element -> element.getUserUuid().equals(organizationMemberEntity.getUserUuid()));
		if (isMember) {
			throw new UserIsAlreadyOrganizationMemberException("L'utilisateur est déjà membre de l'organisation");
		}
	}

	/**
	 * Construction d'un objet membre d'organisation enrichi
	 *
	 * @param member le membre de l'organisation côté strukture
	 * @param user   le user correspondant dans ACL
	 * @return le membre enrichi
	 */
	private OrganizationUserMember buildUserMember(OrganizationMemberEntity member, User user) {

		if (member == null || user == null) {
			return new OrganizationUserMember();
		}

		val memberDto = organizationMemberMapper.entityToDto(member);
		val memberEnriched = new OrganizationUserMember();
		memberEnriched.setUserUuid(memberDto.getUserUuid());
		memberEnriched.setLogin(user.getLogin());
		memberEnriched.setFirstname(user.getFirstname());
		memberEnriched.setLastname(user.getLastname());
		memberEnriched.setLastConnexion(user.getLastConnexion());
		memberEnriched.setRole(memberDto.getRole());
		memberEnriched.setAddedDate(memberDto.getAddedDate());

		return memberEnriched;
	}
}
