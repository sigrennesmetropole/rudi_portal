package org.rudi.microservice.strukture.service.organization;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.bean.UserType;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.projekt.helper.ProjektHelper;
import org.rudi.microservice.strukture.core.bean.Organization;
import org.rudi.microservice.strukture.core.bean.OrganizationMember;
import org.rudi.microservice.strukture.core.bean.OrganizationRole;
import org.rudi.microservice.strukture.core.bean.OrganizationSearchCriteria;
import org.rudi.microservice.strukture.core.bean.PasswordUpdate;
import org.rudi.microservice.strukture.service.StruktureSpringBootTest;
import org.rudi.microservice.strukture.service.exception.CannotRemoveLastAdministratorException;
import org.rudi.microservice.strukture.service.helper.organization.OrganizationMembersHelper;
import org.rudi.microservice.strukture.storage.dao.organization.OrganizationDao;
import org.rudi.microservice.strukture.storage.entity.organization.OrganizationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@StruktureSpringBootTest
class OrganizationServiceUT {

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private OrganizationDao organizationDao;

	@MockBean
	private ACLHelper aclHelper;

	@MockBean
	private ProjektHelper projektHelper;

	@MockBean
	private UtilContextHelper utilContextHelper;

	@MockBean
	OrganizationMembersHelper organizationMembersHelper;

	@MockBean
	DatasetService datasetService;

	private Organization createOrganizationDto() {
		Organization organization = new Organization();
		organization.setName("Fruity Loops");

		LocalDateTime date = LocalDateTime.of(2022, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setOpeningDate(date);
		return organization;
	}

	private User createUserROBOT() {
		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		userOrganisation.setType(UserType.ROBOT);
		return userOrganisation;
	}

	private void mockExternalCalls() throws AppServiceException {
		when(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(any())).thenReturn(true);
		doNothing().when(projektHelper).notifyUserHasBeenAdded(any(), any());
	}

	private void mockAuthenticationData() {
		AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin("login");
		User user = new User().login(authenticatedUser.getLogin()).uuid(UUID.randomUUID());
		when(aclHelper.getUserByLogin(any())).thenReturn(user);
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
	}

	private Organization createTestOrganization() throws AppServiceBadRequestException {
		Organization organization = new Organization();
		organization.setName("Ableton Live");
		organization.setDescription("DAW très bien");
		organization.setUrl("http://www.ableton.com");

		LocalDateTime date = LocalDateTime.of(2022, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setOpeningDate(date);

		LocalDateTime date2 = LocalDateTime.of(2025, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setClosingDate(date2);

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		userOrganisation.setPassword("Azertyu1op!123456");
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		return organizationService.createOrganization(organization);
	}

	private OrganizationMember createOrganizationMember(Organization organization, OrganizationRole role)
			throws Exception {
		val adminUuid = UUID.randomUUID();
		val adminOrgaMember = new OrganizationMember();
		adminOrgaMember.setUuid(organization.getUuid());
		adminOrgaMember.setUserUuid(adminUuid);
		adminOrgaMember.setRole(role);
		adminOrgaMember.setAddedDate(LocalDateTime.now());

		User user = new User().login("randomly").uuid(adminUuid);
		when(organizationMembersHelper.getUserByLoginOrByUuid(any(), eq(adminUuid))).thenReturn(user);

		return adminOrgaMember;
	}

	@BeforeEach
	void init() {
		doNothing().when(projektHelper).notifyUserHasBeenAdded(any(), any());
		doNothing().when(projektHelper).notifyUserHasBeenRemoved(any(), any());
	}

	@AfterEach
	void tearDown() {
		organizationDao.deleteAll();
	}

	@Test
	void createOrganization() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Ableton Live");
		organization.setDescription("DAW très bien");
		organization.setUrl("http://www.ableton.com");

		LocalDateTime date = LocalDateTime.of(2022, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setOpeningDate(date);

		LocalDateTime date2 = LocalDateTime.of(2023, Month.APRIL, 14, 23, 38, 12, 0);
		organization.setClosingDate(date2);

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);

		assertNotNull(created);
		assertNotNull(created.getUuid());

		OrganizationEntity inDb = organizationDao.findByUuid(created.getUuid());
		assertNotNull(inDb);
		assertEquals(organization.getName(), inDb.getName());
		assertEquals(organization.getOpeningDate(), inDb.getOpeningDate());
		assertEquals(organization.getClosingDate(), inDb.getClosingDate());
		assertEquals(organization.getDescription(), inDb.getDescription());
		assertEquals(organization.getUrl(), inDb.getUrl());
	}

	@Test
	void createOrganization_name_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Nom OK");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_name_KO() {

		Organization organization = new Organization();
		organization.setName(
				"Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long "
						+ "Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long Nom trop long");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, () -> organizationService.createOrganization(organization));
	}

	@Test
	void createOrganization_openingDate_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("OpeningDate OK");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_openingDate_KO() {

		Organization organization = new Organization();
		organization.setName("Opening date is missing");

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, () -> organizationService.createOrganization(organization));
	}

	@Test
	void createOrganization_description_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Description OK");
		organization.setDescription("Ceci est ma description, courte.");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_description_KO() {

		Organization organization = new Organization();
		organization.setName("Description KO");
		organization
				.setDescription("Description trop longue de + de 800 caractères ah oui quand même au bout d'un moment"
						+ "j'ai envie de dire enfin voilà quoi après bon. S'il faut meubler autant s'entraîner à la dactilographie n'est-ce pas ? "
						+ "Car ce texte a été écrit à la main pour rendre les tests authentiques de toute manière la façon dont la phrase est tournée"
						+ "n'a que peu d'incidence sur le résultat fonctionnel du TU après tout ? nan je crois que ça se rapproche la ? peut-être pas assez"
						+ "je ne sais pas réellement. J'avoue je m'amuse un peu avec la musique dans les oreilles et tou AAAA quel dommage je devrais"
						+ "écouter du Tenwing ça au moins c'est du lourd, let's go Spotify et Deezer et tout quel artiste de fou et je dis pas ça"
						+ "parce que j'ai un intérêt derrière et tout hehehehehehe. Bon la je fais du padding pour être sûr quoicoubehhhh hein quoi ?"
						+ "hein ? Apagnan enfin ça s'écrit pas comme ça je crois");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, () -> organizationService.createOrganization(organization));
	}

	@Test
	void createOrganization_url_OK() throws AppServiceBadRequestException {

		Organization organization = new Organization();
		organization.setName("Url OK");
		organization.setUrl("https://mavieskoa.com");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		assertNotNull(created);
	}

	@Test
	void createOrganization_url_KO() {

		Organization organization = new Organization();
		organization.setName("Url OK");
		organization.setUrl(
				"https://mavieskoa?query=recherche+trop+longue+faut+pas+faire+ca+surtout+que+je+renvoie+oui.com");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		assertThrows(AppServiceBadRequestException.class, () -> organizationService.createOrganization(organization));
	}

	@Test
	void searchOrganization() throws AppServiceBadRequestException {
		Organization organization = createOrganizationDto();
		User organizationUser = createUserROBOT();
		when(aclHelper.createUser(any())).thenReturn(organizationUser);
		Organization created = organizationService.createOrganization(organization);
		OrganizationSearchCriteria criteria = new OrganizationSearchCriteria();
		criteria.setUuid(created.getUuid());
		Page<Organization> organizations = organizationService.searchOrganizations(criteria, Pageable.unpaged());
		assertTrue(organizations.get().anyMatch(collected -> collected.getName().equals(organization.getName())));
		assertTrue(organizations.get()
				.anyMatch(collected -> collected.getOpeningDate().equals(organization.getOpeningDate())));
	}

	@Test
	void updateOrganization() throws AppServiceException {

		Organization organization = new Organization();
		organization.setName("Novation");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		created.setName("Teenage Engineering");
		created.setOpeningDate(LocalDateTime.now());

		organizationService.updateOrganization(created);

		OrganizationEntity updated = organizationDao.findByUuid(created.getUuid());
		assertNotEquals(updated.getName(), organization.getName());
		assertNotEquals(updated.getOpeningDate(), organization.getOpeningDate());
	}

	@Test
	void deleteOrganization() throws AppServiceException {

		Organization organization = new Organization();
		organization.setName("Sloclap");
		organization.setOpeningDate(LocalDateTime.now());

		User userOrganisation = new User();
		userOrganisation.setUuid(UUID.randomUUID());
		userOrganisation.setLogin(UUID.randomUUID().toString());
		when(aclHelper.createUser(any())).thenReturn(userOrganisation);

		Organization created = organizationService.createOrganization(organization);
		organizationService.deleteOrganization(created.getUuid());

		assertTrue(CollectionUtils.isEmpty(organizationDao.findAll()));
	}

	@Test
	void addOrganisationMember_added_date_custom_OK() throws Exception {
		mockAuthenticationData();
		mockExternalCalls();

		val newOrga = createTestOrganization();
		// Création du membre de l'organisation
		val member = createOrganizationMember(newOrga, OrganizationRole.ADMINISTRATOR);
		// Création de la date custom
		LocalDateTime adminAddedDate = LocalDateTime.of(2023, Month.JANUARY, 8, 13, 19, 16, 0);
		member.setAddedDate(adminAddedDate);
		organizationService.addOrganizationMember(newOrga.getUuid(), member);

		// Vérification que le user de l'organisation est bien créé et que sa date est bien set par défaut.
		val members = organizationService.getOrganizationMembers(newOrga.getUuid());
		assertThat(members).as("L'organisation a des membres").isNotEmpty();
		for (OrganizationMember aMember : members) {
			assertThat(aMember.getAddedDate()).as("La date ne doit pas être nulle").isNotNull();

			assertThat(aMember.getAddedDate().truncatedTo(ChronoUnit.HOURS))
					.as("La date d'ajout doit être égale à celle du jour à l'heure près.")
					.isEqualTo(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS));
		}
	}

	@Test
	@DisplayName("Faire evoluer le rôle d'un editeur à administrateur")
	void updateOrganizationMember_set_to_administrator_OK() throws Exception {
		mockAuthenticationData();
		mockExternalCalls();

		val savedOrganization = createTestOrganization();
		val organizationEditor = createOrganizationMember(savedOrganization, OrganizationRole.EDITOR);

		organizationService.addOrganizationMember(savedOrganization.getUuid(), organizationEditor);
		List<OrganizationMember> members = organizationService.getOrganizationMembers(savedOrganization.getUuid());
		assertThat(members).as("Il existe un membre au moins").isNotEmpty();

		OrganizationMember member = members.stream()
				.filter(organizationMember -> organizationMember.getUserUuid().equals(organizationEditor.getUserUuid()))
				.findFirst().orElse(null);

		assertThat(member.getUserUuid()).as("L'editeur fait bien partie de la liste des membres")
				.isEqualByComparingTo(organizationEditor.getUserUuid());

		// Update du rôle EDITOR => ADMIN
		organizationEditor.setRole(OrganizationRole.ADMINISTRATOR);
		val memberUpdated = organizationService.updateOrganizationMember(savedOrganization.getUuid(),
				organizationEditor.getUserUuid(), organizationEditor);

		assertThat(memberUpdated.getUserUuid()).as("C'est bien le membre visé qui a été modifié")
				.isEqualByComparingTo(organizationEditor.getUserUuid());

		assertThat(memberUpdated.getRole()).as("On est passé de EDITOR => ADMIN")
				.isEqualByComparingTo(OrganizationRole.ADMINISTRATOR);
	}

	@Test
	@DisplayName("Baisser les droits d'un admin à editeur à condition qu'il ne soit pas le dernier admin")
	void updateOrganizationMember_set_to_editor_OK() throws Exception {
		mockAuthenticationData();
		mockExternalCalls();

		val savedOrganization = createTestOrganization();
		val admin1 = createOrganizationMember(savedOrganization, OrganizationRole.ADMINISTRATOR);
		val admin2 = createOrganizationMember(savedOrganization, OrganizationRole.ADMINISTRATOR);

		organizationService.addOrganizationMember(savedOrganization.getUuid(), admin1);
		List<OrganizationMember> members = organizationService.getOrganizationMembers(savedOrganization.getUuid());
		assertThat(members).as("Il existe un membre au moins").isNotEmpty();

		OrganizationMember member = members.stream()
				.filter(organizationMember -> organizationMember.getUserUuid().equals(admin1.getUserUuid())).findFirst()
				.orElse(null);

		assertThat(member.getUserUuid()).as("L'editeur fait bien partie de la liste des membres")
				.isEqualByComparingTo(admin1.getUserUuid());

		// Update du rôle EDITOR => ADMIN
		admin1.setRole(OrganizationRole.EDITOR);
		// On tente de faire passer le dernier admin en EDITOR => Exception
		assertThrows(CannotRemoveLastAdministratorException.class, () -> organizationService
				.updateOrganizationMember(savedOrganization.getUuid(), admin1.getUserUuid(), admin1));

		// On ajoute un second admin puis retente de faire passer le 1 en EDITOR => OK
		organizationService.addOrganizationMember(savedOrganization.getUuid(), admin2);
		val memberUpdated = organizationService.updateOrganizationMember(savedOrganization.getUuid(),
				admin1.getUserUuid(), admin1);
		assertThat(memberUpdated.getUserUuid()).as("C'est bien le membre visé qui a été modifié")
				.isEqualByComparingTo(admin1.getUserUuid());

		assertThat(memberUpdated.getRole()).as("On est passé de EDITOR => ADMIN")
				.isEqualByComparingTo(OrganizationRole.EDITOR);
	}

	@Test
	@DisplayName("Supprimer un membre d'organisation, admin et editeur avec les conditions remplies")
	void removeOrganizationMember_OK() throws Exception {
		mockAuthenticationData();
		mockExternalCalls();

		val savedOrganization = createTestOrganization();
		val admin1 = createOrganizationMember(savedOrganization, OrganizationRole.ADMINISTRATOR);
		val admin2 = createOrganizationMember(savedOrganization, OrganizationRole.ADMINISTRATOR);
		val editor1 = createOrganizationMember(savedOrganization, OrganizationRole.EDITOR);
		organizationService.addOrganizationMember(savedOrganization.getUuid(), admin1);
		organizationService.addOrganizationMember(savedOrganization.getUuid(), admin2);
		organizationService.addOrganizationMember(savedOrganization.getUuid(), editor1);

		var oldMembers = organizationService.getOrganizationMembers(savedOrganization.getUuid());
		assertThat(oldMembers.size()).as("Il y a 3 membres, les 2 ajoutés + le ROBOT").isEqualTo(4);

		// Suppression d'un ADMINISTRATOR
		organizationService.removeOrganizationMembers(savedOrganization.getUuid(), admin1.getUserUuid());
		var newMembers = organizationService.getOrganizationMembers(savedOrganization.getUuid());

		assertThat(newMembers.size()).as("On ne doit avoir supprimé qu'un seul membre")
				.isEqualTo(oldMembers.size() - 1);

		assertThat(newMembers.stream().map(OrganizationMember::getUserUuid).collect(Collectors.toList()))
				.as("La liste ne doit plus contenir le membre supprimé").doesNotContain(admin1.getUserUuid());

		// Suppression d'un EDITOR
		organizationService.removeOrganizationMembers(savedOrganization.getUuid(), editor1.getUserUuid());
		var membersAfterRemoveEditor = organizationService.getOrganizationMembers(savedOrganization.getUuid());

		assertThat(membersAfterRemoveEditor.stream().map(OrganizationMember::getUserUuid).collect(Collectors.toList()))
				.as("La liste ne doit plus contenir les membres supprimés (1 ADMIN et 1 EDITOR)")
				.doesNotContain(admin1.getUserUuid(), editor1.getUserUuid());
	}

	@Test
	@DisplayName("S'assurer qu'on ne peut pas supprimer le dernier admin")
	void removeOrganizationMember_last_admin_NOK() throws Exception {
		mockAuthenticationData();
		mockExternalCalls();

		val savedOrganization = createTestOrganization();
		val admin1 = createOrganizationMember(savedOrganization, OrganizationRole.ADMINISTRATOR);
		organizationService.addOrganizationMember(savedOrganization.getUuid(), admin1);

		var oldMembers = organizationService.getOrganizationMembers(savedOrganization.getUuid());
		assertThat(oldMembers.size()).as("Il y a 2 membres, l'admin + le ROBOT").isEqualTo(2);

		// Tentative de uppression de l'ADMINISTRATOR
		// On ne peut supprimer le dernier ADMIN
		assertThrows(CannotRemoveLastAdministratorException.class,
				() -> organizationService.removeOrganizationMembers(savedOrganization.getUuid(), admin1.getUserUuid()));

		var newMembers = organizationService.getOrganizationMembers(savedOrganization.getUuid());

		assertThat(newMembers.size()).as("L'admin n'a pas été supprimé et aucun autre membre ne l'a été")
				.isEqualTo(oldMembers.size());
	}

	@Test
	void updateOrganizationMember_KO_when_updating_another_member_from_memberDtoInfo() throws AppServiceException {

		Organization organization = createTestOrganization();

		UUID userUuidModified = UUID.randomUUID();
		UUID userUuidFromAnotherUser = UUID.randomUUID();

		OrganizationMember updateDto = new OrganizationMember();
		updateDto.setUuid(organization.getUuid());
		updateDto.setUserUuid(userUuidModified);
		updateDto.setRole(OrganizationRole.ADMINISTRATOR);

		when(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(any())).thenReturn(true);

		// J'essaye de MAJ les infos d'un autre membre (membre 1) alors que j'ai un DTO qui concerne quelqu'un d'autre (membre 2)
		assertThrows(AppServiceBadRequestException.class, () -> organizationService
				.updateOrganizationMember(organization.getUuid(), userUuidFromAnotherUser, updateDto));
	}

	@Test
	void updateOrganizationMember_KO_when_updating_the_member_to_another_organization() throws AppServiceException {

		Organization organization = createTestOrganization();
		Organization anotherOne = createTestOrganization();

		UUID userUuidModified = UUID.randomUUID();

		OrganizationMember updateDto = new OrganizationMember();
		updateDto.setUuid(organization.getUuid());
		updateDto.setUserUuid(userUuidModified);
		updateDto.setRole(OrganizationRole.ADMINISTRATOR);

		when(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(any())).thenReturn(true);

		// J'essaye de MAJ les infos d'un membre d'une autre organisation ( orga 1) alors que j'ai un DTO qui concerne l'orga 2
		assertThrows(AppServiceBadRequestException.class,
				() -> organizationService.updateOrganizationMember(anotherOne.getUuid(), userUuidModified, updateDto));
	}

	@Test
	void updateOrganizationMember_unauthorized_when_not_administrator_of_organization() throws AppServiceException {

		Organization organization = createTestOrganization();
		UUID userUuidModified = UUID.randomUUID();

		OrganizationMember updateDto = new OrganizationMember();
		updateDto.setUuid(organization.getUuid());
		updateDto.setUserUuid(userUuidModified);
		updateDto.setRole(OrganizationRole.ADMINISTRATOR);

		when(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(any())).thenReturn(false);

		// Je suis pas administrateur de l'orga, j'ai pas le droit
		assertThrows(AppServiceUnauthorizedException.class, () -> organizationService
				.updateOrganizationMember(organization.getUuid(), userUuidModified, updateDto));
	}

	@Test
	void removeOrganizationMembers_does_not_delete_members_if_it_fails() throws AppServiceException {

		Organization organization = createTestOrganization();
		UUID userRemovedUuid = UUID.randomUUID();

		var oldMembers = organizationService.getOrganizationMembers(organization.getUuid());

		when(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(any())).thenReturn(false);
		assertThrows(AppServiceUnauthorizedException.class,
				() -> organizationService.removeOrganizationMembers(organization.getUuid(), userRemovedUuid));

		var newMembers = organizationService.getOrganizationMembers(organization.getUuid());

		// On ne doit avoir supprimé aucun membre
		assertEquals(oldMembers, newMembers);
	}

	@Test
	void updateUserOrganizationPassword_forbidden_when_not_admin_of_organization() throws AppServiceException {
		createTestOrganization();
		when(organizationMembersHelper.isAuthenticatedUserOrganizationAdministrator(any())).thenReturn(false);
		PasswordUpdate passwordUpdate = new PasswordUpdate();
		passwordUpdate.setOldPassword("manemajeff");
		passwordUpdate.setNewPassword("Peut1mp0rt3!");
		assertThrows(AppServiceForbiddenException.class,
				() -> organizationService.updateUserOrganizationPassword(UUID.randomUUID(), passwordUpdate));
	}
}
