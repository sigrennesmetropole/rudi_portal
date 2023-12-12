package org.rudi.microservice.projekt.service.project;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.RoleCodes;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.OwnerType;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.ProjectSpringBootTest;
import org.rudi.microservice.projekt.service.replacer.TransientDtoReplacerTest;
import org.rudi.microservice.projekt.storage.dao.newdatasetrequest.NewDatasetRequestDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class NewDatasetRequestServiceUT {

	private static final KnownProject PROJET_LAMPADAIRES = new KnownProject("lampadaires",
			"Projet de comptage des lampadaires");

	private static final KnownProject PROJECT_IN_NAME_OF_ORGANIZATION = new KnownProject(
			"project_for_organization",
			"Projet d'une organisation donc créé par un membre d'organisation. La création par le membre ne fait pas de lui le porteur de projet");

	private static final String COMMENTAIRE_KEY_MAP = "messageToProjectOwner";
	private static final String DATE_KEY_MAP = "commentDate";

	private final NewDatasetRequestService newDatasetRequestService;
	private final JsonResourceReader jsonResourceReader;
	private final List<TransientDtoReplacerTest> transientDtoReplacers;
	private final ProjectService projectService;
	private final ProjectDao projectDao;
	private final NewDatasetRequestDao newDatasetRequestDao;

	private final FormHelper formHelper;
	
	@MockBean
	private ACLHelper aclHelper;
	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private OrganizationHelper organizationHelper;
//	@MockBean
//	private RolesHelper rolesHelper;

	@AfterEach
	void tearDown() {
		newDatasetRequestDao.deleteAll();
		projectDao.deleteAll();
	}

	@Data
	private static class KnownProject {
		private final String file;
		private final String title;

		String getJsonPath() {
			return "projects/" + file + ".json";
		}
	}

	private Project createProject(KnownProject knownProject) throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(knownProject.getJsonPath(), Project.class);
		createEntities(project);

		mockAuthenticatedUserToCreateProject(project);

		return projectService.createProject(project);
	}

	private void createEntities(Project project) throws AppServiceException {
		for (final TransientDtoReplacerTest getterOrCreator : transientDtoReplacers) {
			getterOrCreator.replaceDtoFor(project);
		}
	}

	private void mockAuthenticatedUserToCreateProject(Project project) throws AppServiceUnauthorizedException, GetOrganizationMembersException {
		mockAuthenticatedUserFromManager(project.getOwnerUuid(), project.getOwnerType().equals(OwnerType.ORGANIZATION));

	}

	private void mockAuthenticatedUserOtherUser(UUID managerUserUuid, List<String> roleCodes) throws AppServiceUnauthorizedException {
		final User user = new User().login("shakira").uuid(managerUserUuid);

		final List<Role> roles = new ArrayList<>();

		for(String code : roleCodes){
			Role userRole = new Role();
			userRole.setCode(code);
			roles.add(userRole);
		}

		user.setRoles(roles);

		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(aclHelper.getAuthenticatedUser()).thenReturn(user);
		when(aclHelper.getAuthenticatedUserUuid()).thenReturn(managerUserUuid);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
	}

	private void mockAuthenticatedUserFromManager(UUID managerUserUuid, boolean isOrganization) throws AppServiceUnauthorizedException, GetOrganizationMembersException {
		final User user = new User();
		user.setLogin("mpokora");

		//Si le projet est créé par un user et non au nom d'une organization.
		if(!isOrganization){
			user.setUuid(managerUserUuid);
		}
		else {
			//Un UUID au hasard pour ne pas avoir le même que celui de l'orga
			user.setUuid(UUID.randomUUID());
			when(organizationHelper.organizationContainsUser(managerUserUuid, user.getUuid())).thenReturn(true);
		}

		Role userRole = new Role();
		userRole.setCode(RoleCodes.USER);

		final List<Role> roles = List.of(userRole);
		user.setRoles(roles);

		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(aclHelper.getAuthenticatedUser()).thenReturn(user);
		when(aclHelper.getAuthenticatedUserUuid()).thenReturn(managerUserUuid);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);


	}

	private NewDatasetRequest createNewDatasetRequest(UUID uuid, String title, String description, LocalDateTime date){
		NewDatasetRequest request = new NewDatasetRequest();
		request.setUuid(uuid);
		request.setTitle(title);
		request.setDescription(description);
		request.setCreationDate(date);

		return request;
	}

	@Test
	@DisplayName("Je récupère une décision en tant que Owner du projet, mais elle ne contient aucun commentaire")
	void getDecisionInformationsEmptyData() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuis = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuis,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		// Le forumlaire retourné doit être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire retourné doit être null, car aucune information n'a été saisie par que qui ce soit pour l'instant.")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décision en tant que Owner du projet qui contient commentaire et date")
	void getDecisionInformationsFullFilledData() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date, NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();
		// Le formulaire retourné doit contenir au moins une section
		assertThat(decision.getSections()).as("Le form doit contenir au moins une section").matches(sections -> !sections.isEmpty());

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section)
				.as("La section ne doit pas être null")
				.isNotNull()
				.as("La section doit contenir des field (commentaire et date)")
				.matches(s -> !s.getFields().isEmpty());

		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field date doit contenir la bonne date")
				.matches((f -> f.isPresent() && LocalDateTime.parse(f.get().getValues().get(0)).equals(date)));
	}

	@Test
	@DisplayName("Je récupère une décision en tant que Owner du projet qui ne contient qu'une date, donc le résultat est null")
	void getDecisionInformationsOnlyDate() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// Le forumlaire retourné doit être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire ne dois rien contenir, car le champ commentaire n'est pas renseigné.")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décision en tant que Owner du projet qui contient commentaire mais pas de date")
	void getDecisionInformationsOnlyComment() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();
		// Le formulaire retourné doit contenir au moins une section
		assertThat(decision.getSections()).as("Le form doit contenir au moins une section").matches(sections -> !sections.isEmpty());

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section)
				.as("La section ne doit pas être null")
				.isNotNull()
				.as("La section doit contenir des field (commentaire et date)")
				.matches(s -> !s.getFields().isEmpty());

		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date est présent")
				.matches(Optional::isPresent)
				.as("Le field est présent, mais il doit être null")
				.matches(f -> f.isPresent() && f.get().getValues() == null);
	}

	@Test
	@DisplayName("Je tente de récupérer une décision concernant un linkedDataset et un ProjectUuid non liés")
	void getDecisionInformationsNotRelatedUuids() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuis = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuis,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		assertThrows(AppServiceNotFoundException.class, ()-> newDatasetRequestService.getDecisionInformations(UUID.randomUUID(), requestUuid));
		assertThrows(AppServiceNotFoundException.class, ()-> newDatasetRequestService.getDecisionInformations(projectUuid, UUID.randomUUID()));
		assertThrows(AppServiceNotFoundException.class, ()-> newDatasetRequestService.getDecisionInformations(UUID.randomUUID(), UUID.randomUUID()));
	}

	@Test
	@DisplayName("Je tente de récupérer une décision, mais authentifié en tant qu'Administrateur")
	void getDecisionInformationAsAdministrator() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date, NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant qu'Administrateur
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.ADMINISTRATOR));

		// Le forumlaire retourné ne doit pas être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();
		// Le formulaire retourné doit contenir au moins une section
		assertThat(decision.getSections()).as("Le form doit contenir au moins une section").matches(sections -> !sections.isEmpty());

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section)
				.as("La section ne doit pas être null")
				.isNotNull()
				.as("La section doit contenir des field (commentaire et date)")
				.matches(s -> !s.getFields().isEmpty());

		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field date doit contenir la bonne date")
				.matches((f -> f.isPresent() && LocalDateTime.parse(f.get().getValues().get(0)).equals(date)));
	}

	@Test
	@DisplayName("Je tente de récupérer une décision, mais authentifié en tant qu'Animateur")
	void getDecisionInformationAsModerator() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date, NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant qu'Animateur
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.MODERATOR));

		// Le forumlaire retourné ne doit pas être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();
		// Le formulaire retourné doit contenir au moins une section
		assertThat(decision.getSections()).as("Le form doit contenir au moins une section").matches(sections -> !sections.isEmpty());

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section)
				.as("La section ne doit pas être null")
				.isNotNull()
				.as("La section doit contenir des field (commentaire et date)")
				.matches(s -> !s.getFields().isEmpty());

		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field date doit contenir la bonne date")
				.matches((f -> f.isPresent() && LocalDateTime.parse(f.get().getValues().get(0)).equals(date)));
	}

	@Test
	@DisplayName("Je tente de récupérer une décision, mais authentifié en tant qu'utilisateur extérieur au projet")
	void getDecisionInformationAsOtherUser() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date, NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant que USER non lié au projet
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.USER));

		// La fonction doit retourner une exception car l'utilisateur n'a pas les accès
		assertThrows(AppServiceUnauthorizedException.class, () -> newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid));
	}

	@Test
	@DisplayName("Je tente de récupérer une décision, mais authentifié en tant qu'utilisateur extérieur au projet sans role")
	void getDecisionInformationAsOtherUserWithNoRole() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date, NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant qu'Administrateur
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of());

		// La fonction doit retourner une exception car l'utilisateur n'a pas les accès
		assertThrows(AppServiceUnauthorizedException.class, () -> newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid));
	}

	@Test
	@DisplayName("Je récupère une décision en tant que membre de l'organisation owner du projet, mais elle ne contient aucun commentaire")
	void getDecisionInformationsEmptyDataForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuis = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuis,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		// Le forumlaire retourné doit être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire retourné doit être null, car aucune information n'a été saisie par que qui ce soit pour l'instant.")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décision en tant que membre de l'organisation owner du projet qui contient commentaire et date")
	void getDecisionInformationsFullFilledDataForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final UUID projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date, NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();
		// Le formulaire retourné doit contenir au moins une section
		assertThat(decision.getSections()).as("Le form doit contenir au moins une section").matches(sections -> !sections.isEmpty());

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section)
				.as("La section ne doit pas être null")
				.isNotNull()
				.as("La section doit contenir des field (commentaire et date)")
				.matches(s -> !s.getFields().isEmpty());

		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field date doit contenir la bonne date")
				.matches((f -> f.isPresent() && LocalDateTime.parse(f.get().getValues().get(0)).equals(date)));
	}

	@Test
	@DisplayName("Je récupère une décision en tant que membre de l'organisation owner du projet qui ne contient qu'une date, donc le résultat est null")
	void getDecisionInformationsOnlyDateForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute la date du jour
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.DATE_KEY_MAP, date);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// Le forumlaire retourné doit être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire ne dois rien contenir, car le champ commentaire n'est pas renseigné.")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décisionen tant que membre de l'organisation owner du projet qui contient commentaire mais pas de date")
	void getDecisionInformationsOnlyCommentForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final var projectUuid = createdProject.getUuid();

		// Créations des la requête de nouvelle donnée
		final var rUuid = UUID.randomUUID();
		final var request = createNewDatasetRequest(rUuid,
				"Smoke on the Water",
				"Une chanson sur l'incendie d'un casino à Montreux",
				LocalDateTime.now());

		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);
		val requestUuid = requestCreated.getUuid();

		val newDatasetRequestEntity = newDatasetRequestDao.findByUuid(requestUuid);
		assertThat(newDatasetRequestEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = newDatasetRequestEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire sans la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		Map<String, Object> data = Map.of(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		newDatasetRequestEntity.setData(formHelper.deshydrateData(data));
		newDatasetRequestDao.save(newDatasetRequestEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = newDatasetRequestService.getDecisionInformations(projectUuid, requestUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();
		// Le formulaire retourné doit contenir au moins une section
		assertThat(decision.getSections()).as("Le form doit contenir au moins une section").matches(sections -> !sections.isEmpty());

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section)
				.as("La section ne doit pas être null")
				.isNotNull()
				.as("La section doit contenir des field (commentaire et date)")
				.matches(s -> !s.getFields().isEmpty());

		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(NewDatasetRequestServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date est présent")
				.matches(Optional::isPresent)
				.as("Le field est présent, mais il doit être null")
				.matches(f -> f.isPresent() && f.get().getValues() == null);
	}
}
