package org.rudi.microservice.strukture.service.organization;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserType;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.strukture.core.bean.OrganizationMemberType;
import org.rudi.microservice.strukture.core.bean.OrganizationMembersSearchCriteria;
import org.rudi.microservice.strukture.core.bean.OrganizationUserMember;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.exception.UserIsAlreadyOrganizationMemberException;
import org.rudi.microservice.strukture.service.exception.UserNotFoundException;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersHelper;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationMemberEntity;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@StruktureSpringBootTest
class OrganizationMembersHelperTestUT {

	@Autowired
	private OrganizationMembersHelper organizationMembersHelper;

	@Autowired
	private OrganizationDao organizationDao;

	@MockBean
	private ACLHelper aclHelper;

	@MockBean
	private UtilContextHelper utilContextHelper;

	@MockBean
	DatasetService datasetService;

	@AfterEach
	public void cleanData() {
		organizationDao.deleteAll();
	}

	@Test
	void isAuthenticatedUserAuthorizedToSearchMembers_ok_on_organizationAdministrator() throws AppServiceException {

		AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin("login");

		User user = new User().login(authenticatedUser.getLogin()).uuid(UUID.randomUUID());

		OrganizationMemberEntity organizationMember = new OrganizationMemberEntity();
		organizationMember.setUserUuid(user.getUuid());
		organizationMember.setRole(OrganizationRole.ADMINISTRATOR);
		organizationMember.setAddedDate(LocalDateTime.now());

		OrganizationEntity organization = new OrganizationEntity();
		organization.setUuid(UUID.randomUUID());
		organization.setOpeningDate(LocalDateTime.now());
		organization.setDescription("organisation");
		organization.setName("mon organisation");
		organization.setMembers(Set.of(organizationMember));
		organizationDao.save(organization);

		when(aclHelper.getUserByLogin(any())).thenReturn(user);
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		assertTrue(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(organization.getUuid()));
	}

	@Test
	void isAuthenticatedUserAuthorizedToSearchMembers_ko_on_organizationEditor() throws AppServiceException {

		AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin("login");

		User user = new User().login(authenticatedUser.getLogin()).uuid(UUID.randomUUID());

		OrganizationMemberEntity organizationMember = new OrganizationMemberEntity();
		organizationMember.setUserUuid(user.getUuid());
		organizationMember.setRole(OrganizationRole.EDITOR);
		organizationMember.setAddedDate(LocalDateTime.now());

		OrganizationEntity organization = new OrganizationEntity();
		organization.setUuid(UUID.randomUUID());
		organization.setOpeningDate(LocalDateTime.now());
		organization.setDescription("organisation");
		organization.setName("mon organisation");
		organization.setMembers(Set.of(organizationMember));
		organizationDao.save(organization);

		when(aclHelper.getUserByLogin(any())).thenReturn(user);
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		assertFalse(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(organization.getUuid()));
	}

	@Test
	void isAuthenticatedUserAuthorizedToSearchMembers_ko_on_userNotFromOrganization() throws AppServiceException {

		AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin("login");

		User user = new User().login(authenticatedUser.getLogin()).uuid(UUID.randomUUID());

		OrganizationEntity organization = new OrganizationEntity();
		organization.setUuid(UUID.randomUUID());
		organization.setOpeningDate(LocalDateTime.now());
		organization.setDescription("organisation");
		organization.setName("mon organisation");
		organizationDao.save(organization);

		when(aclHelper.getUserByLogin(any())).thenReturn(user);
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		assertFalse(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(organization.getUuid()));
	}

	@Test
	void splitMembersHavingCorrespondingUsers_ok_for_splitting_members() {

		User realUser = new User().login("onsenfiche").uuid(UUID.randomUUID());
		List<User> realUsers = List.of(realUser);
		OrganizationMemberEntity goodMember = new OrganizationMemberEntity();
		goodMember.setUserUuid(realUser.getUuid());
		OrganizationMemberEntity badMember = new OrganizationMemberEntity();
		List<OrganizationMemberEntity> organizationMembers = List.of(goodMember, badMember);

		Map<Boolean, List<OrganizationMemberEntity>> membersPartitioned = organizationMembersHelper
				.splitMembersHavingCorrespondingUsers(organizationMembers, realUsers);

		List<OrganizationMemberEntity> goodMembers = membersPartitioned.get(true);
		List<OrganizationMemberEntity> badMembers = membersPartitioned.get(false);

		List<UUID> usersUuids = realUsers.stream().map(User::getUuid).collect(Collectors.toList());
		long goodMembersWithoutCorrespondingUsers = goodMembers.stream()
				.filter(member -> !usersUuids.contains(member.getUserUuid())).count();
		assertEquals(0, goodMembersWithoutCorrespondingUsers);

		long badMembersWithCorrespondingUsers = badMembers.stream()
				.filter(member -> usersUuids.contains(member.getUserUuid())).count();
		assertEquals(0, badMembersWithCorrespondingUsers);
	}

	@Test
	void mergeMembersAndUsers_ok_for_mapping() {

		OrganizationMemberEntity member = new OrganizationMemberEntity();
		member.setUserUuid(UUID.randomUUID());
		member.setAddedDate(LocalDateTime.now().minusDays(1));
		member.setRole(OrganizationRole.EDITOR);
		List<OrganizationMemberEntity> members = List.of(member);
		Map<UUID, OrganizationMemberEntity> membersByUserUuid = members.stream()
				.collect(Collectors.toMap(OrganizationMemberEntity::getUserUuid, Function.identity()));

		User user = new User().login("okmec").firstname("monprénom").lastname("monnomdefamille")
				.uuid(member.getUserUuid()).lastConnexion(LocalDateTime.now());
		List<User> users = List.of(user);
		Map<UUID, User> usersByUuid = users.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));

		List<OrganizationUserMember> membersEnriched = organizationMembersHelper.mergeMembersAndUsers(members, users);

		assertFalse(CollectionUtils.isEmpty(membersEnriched));

		for (OrganizationUserMember memberEnriched : membersEnriched) {
			OrganizationMemberEntity memberMerged = membersByUserUuid.get(memberEnriched.getUserUuid());
			User userMerged = usersByUuid.get(memberEnriched.getUserUuid());

			boolean uuidOk = memberEnriched.getUserUuid() != null
					&& memberEnriched.getUserUuid().equals(memberMerged.getUserUuid())
					&& memberEnriched.getUserUuid().equals(userMerged.getUuid());
			assertTrue(uuidOk);

			boolean loginOk = StringUtils.equals(memberEnriched.getLogin(), userMerged.getLogin());
			assertTrue(loginOk);

			boolean lastNameOk = StringUtils.equals(memberEnriched.getLastname(), userMerged.getLastname());
			assertTrue(lastNameOk);

			boolean firstNameOk = StringUtils.equals(memberEnriched.getFirstname(), userMerged.getFirstname());
			assertTrue(firstNameOk);

			boolean addedDateOk = memberEnriched.getAddedDate() != null
					&& memberEnriched.getAddedDate().equals(memberMerged.getAddedDate());
			assertTrue(addedDateOk);

			boolean lastConnexionOk = memberEnriched.getLastConnexion() != null
					&& memberEnriched.getLastConnexion().equals(userMerged.getLastConnexion());
			assertTrue(lastConnexionOk);

			boolean roleOk = memberEnriched.getRole() != null
					&& memberEnriched.getRole().ordinal() == memberMerged.getRole().ordinal();
			assertTrue(roleOk);
		}
	}

	@Test
	void searchCorrespondingUsers_does_not_search_on_empty_or_no_user_uuids() {
		OrganizationMembersSearchCriteria criteria = new OrganizationMembersSearchCriteria();
		List<User> users = organizationMembersHelper.searchCorrespondingUsers(Collections.emptyList(), criteria);
		assertTrue(CollectionUtils.isEmpty(users));

		criteria.setSearchText("");
		users = organizationMembersHelper.searchCorrespondingUsers(Collections.emptyList(), criteria);
		assertTrue(CollectionUtils.isEmpty(users));

		criteria.setType(OrganizationMemberType.PERSON);
		users = organizationMembersHelper.searchCorrespondingUsers(Collections.emptyList(), criteria);
		assertTrue(CollectionUtils.isEmpty(users));
	}

	@Test
	void searchCorrespondingUsers_searchs_everyone_by_default() {

		User person = new User().login("person").type(UserType.PERSON).uuid(UUID.randomUUID());
		User robot = new User().login("robot").type(UserType.ROBOT).uuid(UUID.randomUUID());
		OrganizationMembersSearchCriteria criteria = new OrganizationMembersSearchCriteria();

		OrganizationMemberEntity member1 = new OrganizationMemberEntity();
		member1.setUserUuid(person.getUuid());
		OrganizationMemberEntity member2 = new OrganizationMemberEntity();
		member2.setUserUuid(robot.getUuid());

		when(aclHelper.searchUsersWithCriteria(eq(List.of(person.getUuid(), robot.getUuid())), any(), eq(""),any()))
				.thenReturn(List.of(person, robot));
		when(aclHelper.searchUsersWithCriteria(eq(List.of(person.getUuid(), robot.getUuid())), any(), eq(null),any()))
				.thenReturn(List.of(person, robot));
		List<User> users = organizationMembersHelper.searchCorrespondingUsers(List.of(member1, member2), criteria);
		assertFalse(CollectionUtils.isEmpty(users));
		assertEquals(2, users.size());
	}

	@Test
	void searchCorrespondingUsers_filters_by_person() {

		User person = new User().login("person").type(UserType.PERSON).uuid(UUID.randomUUID());
		User robot = new User().login("robot").type(UserType.ROBOT).uuid(UUID.randomUUID());

		OrganizationMembersSearchCriteria criteria = new OrganizationMembersSearchCriteria();
		criteria.setType(OrganizationMemberType.PERSON);

		OrganizationMemberEntity member1 = new OrganizationMemberEntity();
		member1.setUserUuid(person.getUuid());
		OrganizationMemberEntity member2 = new OrganizationMemberEntity();
		member2.setUserUuid(robot.getUuid());

		when(aclHelper.searchUsersWithCriteria(eq(List.of(person.getUuid(), robot.getUuid())), any(),
				eq(UserType.PERSON.getValue()),any())).thenReturn(List.of(person));
		List<User> users = organizationMembersHelper.searchCorrespondingUsers(List.of(member1, member2), criteria);
		assertFalse(CollectionUtils.isEmpty(users));
		assertEquals(1, users.size());
		User retrieved = users.get(0);
		assertEquals(person.getUuid(), retrieved.getUuid());
	}

	@Test
	void searchCorrespondingUsers_filters_by_robot() {

		User person = new User().login("person").type(UserType.PERSON).uuid(UUID.randomUUID());
		User robot = new User().login("robot").type(UserType.ROBOT).uuid(UUID.randomUUID());

		OrganizationMembersSearchCriteria criteria = new OrganizationMembersSearchCriteria();
		criteria.setType(OrganizationMemberType.ROBOT);

		OrganizationMemberEntity member1 = new OrganizationMemberEntity();
		member1.setUserUuid(person.getUuid());
		OrganizationMemberEntity member2 = new OrganizationMemberEntity();
		member2.setUserUuid(robot.getUuid());

		when(aclHelper.searchUsersWithCriteria(eq(List.of(person.getUuid(), robot.getUuid())), any(),
				eq(UserType.ROBOT.getValue()),any())).thenReturn(List.of(robot));
		List<User> users = organizationMembersHelper.searchCorrespondingUsers(List.of(member1, member2), criteria);
		assertFalse(CollectionUtils.isEmpty(users));
		assertEquals(1, users.size());
		User retrieved = users.get(0);
		assertEquals(robot.getUuid(), retrieved.getUuid());
	}

	@Test
	@DisplayName("Cherche un user dans ACL à partir de son login ou de son uuid. On ne passe pas aucun des 2 paramètres de recherche ici")
	void getUserByLoginOrByUuid_none_parameter() {
		assertThrowsExactly(MissingParameterException.class,
				() -> organizationMembersHelper.getUserByLoginOrByUuid("", null));
		assertThrowsExactly(MissingParameterException.class,
				() -> organizationMembersHelper.getUserByLoginOrByUuid(null, null));
	}

	@Test
	@DisplayName("Cherche un user dans ACL à partir de son login ou de son uuid. On passe le userUuid comme paramètre de recherche ici")
	void getUserByLoginOrByUuid_user_uuid_parameter() throws AppServiceException {
		User myUser = new User().login("user-test").uuid(UUID.fromString("2acbc550-81eb-4710-8ee3-15c4af42c74a"));
		when(aclHelper.getUserByUUID(UUID.fromString("2acbc550-81eb-4710-8ee3-15c4af42c74a"))).thenReturn(myUser);

		// On recherche un userUuid aléatoire => UserNotFoundException
		assertThrowsExactly(UserNotFoundException.class,
				() -> organizationMembersHelper.getUserByLoginOrByUuid("", UUID.randomUUID()));

		// On recherche un userUuid correct mais un login aléatoire => UserNotFoundException
		assertThrowsExactly(UserNotFoundException.class, () -> organizationMembersHelper.getUserByLoginOrByUuid("",
				UUID.fromString("2acbc550-81eb-4710-8ee3-15c4af42c74a")));

		assertThat(organizationMembersHelper
				.getUserByLoginOrByUuid(null, UUID.fromString("2acbc550-81eb-4710-8ee3-15c4af42c74a")).getUuid())
						.as("L'uuid passé correspond à notre user mocké et on ne passe pas de login")
						.isEqualByComparingTo(myUser.getUuid());
	}

	@Test
	@DisplayName("Cherche un user dans ACL à partir de son login ou de son uuid. On passe le login comme paramètre de recherche ici")
	void getUserByLoginOrByUuid_login_parameter() throws AppServiceException {
		User myUser = new User().login("user-test").uuid(UUID.randomUUID());
		when(aclHelper.getUserByLogin("user-test")).thenReturn(myUser);

		// On recherche un login aléatoire => UserNotFoundException
		assertThrowsExactly(UserNotFoundException.class,
				() -> organizationMembersHelper.getUserByLoginOrByUuid("azerty", null));

		// On recherche un login correct mais un userUuid aléatoire => UserNotFoundException
		assertThrowsExactly(UserNotFoundException.class,
				() -> organizationMembersHelper.getUserByLoginOrByUuid("user-test", UUID.randomUUID()));

		assertThat(organizationMembersHelper.getUserByLoginOrByUuid("user-test", null).getUuid())
				.as("Le login passé correspond à notre user mocké").isEqualByComparingTo(myUser.getUuid());
	}

	@Test
	@DisplayName("Cherche un user dans ACL à partir de son login ou de son uuid. On passe les 2 paramètres ici alors l'uuid prime dans la recherche")
	void getUserByLoginOrByUuid_login_and_uuid_parameter() throws AppServiceException {
		User myUser = new User().login("user-test").uuid(UUID.fromString("2acbc550-81eb-4710-8ee3-15c4af42c74a"));
		when(aclHelper.getUserByLogin("user-test")).thenReturn(myUser);
		when(aclHelper.getUserByUUID(UUID.fromString("2acbc550-81eb-4710-8ee3-15c4af42c74a"))).thenReturn(myUser);

		// On passe un login aléatoire et un uuid alétoire => UserNotFoundException
		assertThrowsExactly(UserNotFoundException.class,
				() -> organizationMembersHelper.getUserByLoginOrByUuid("azerty", UUID.randomUUID()));

		assertThat(organizationMembersHelper
				.getUserByLoginOrByUuid("user-test", UUID.fromString("2acbc550-81eb-4710-8ee3-15c4af42c74a")).getUuid())
						.as("Le login passé est correct et l'uuid aussi, notre user mocké est donc bien retrouvé")
						.isEqualByComparingTo(myUser.getUuid());
	}

	@Test
	@DisplayName("Teste l'égalité entre 2 membres d'organisation")
	void organizationMemberEquality() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		OrganizationMemberEntity member1 = new OrganizationMemberEntity();
		member1.setAddedDate(LocalDateTime.now());
		member1.setUserUuid(uuid1);
		member1.setRole(OrganizationRole.EDITOR);

		OrganizationMemberEntity member2 = new OrganizationMemberEntity();
		member2.setAddedDate(LocalDateTime.now());
		member2.setUserUuid(uuid2);
		member2.setRole(OrganizationRole.EDITOR);

		OrganizationMemberEntity member3 = new OrganizationMemberEntity();
		member3.setAddedDate(LocalDateTime.now());
		member3.setUserUuid(uuid2);
		member3.setRole(OrganizationRole.ADMINISTRATOR);

		assertThat(member1.equals(member1)).as("Les objets sont identiques").isTrue();
		assertThat(member1.equals(member2)).as("Les deux membres sont distincts").isFalse();
		assertThat(member2.equals(member3)).as("Les 2 membres sont identiques car ils ont des userUuid identiques")
				.isTrue();
	}

	@Test
	@DisplayName("Assurer l'unicité des membres dans une organisation")
	public void checkUserIsNotMember() {
		UUID uuid1 = UUID.randomUUID();
		UUID uuid2 = UUID.randomUUID();

		OrganizationMemberEntity member1 = new OrganizationMemberEntity();
		member1.setAddedDate(LocalDateTime.now());
		member1.setUserUuid(uuid1);
		member1.setRole(OrganizationRole.EDITOR);

		OrganizationMemberEntity member2 = new OrganizationMemberEntity();
		member2.setAddedDate(LocalDateTime.now());
		member2.setUserUuid(uuid2);
		member2.setRole(OrganizationRole.EDITOR);

		OrganizationMemberEntity member3 = new OrganizationMemberEntity();
		member3.setAddedDate(LocalDateTime.now());
		member3.setUserUuid(uuid2);
		member3.setRole(OrganizationRole.ADMINISTRATOR);

		OrganizationEntity organization = new OrganizationEntity();
		organization.setUuid(UUID.randomUUID());
		organization.setOpeningDate(LocalDateTime.now());
		organization.setName("Blablabla pookie xD");

		organization.getMembers().add(member1);

		OrganizationEntity saved = organizationDao.save(organization);
		// Le membre2 n'a pas encore été ajouté à l'organisation => pas d'exception throw
		assertDoesNotThrow(() -> organizationMembersHelper.checkUserIsNotMember(saved, member2));
		saved.getMembers().add(member2);
		// On a ajouté le membre et tentons d'ajouter le 3 sauf que membre2.equals(membre2) => Exception
		assertThrowsExactly(UserIsAlreadyOrganizationMemberException.class,
				() -> organizationMembersHelper.checkUserIsNotMember(saved, member3));
	}
}
