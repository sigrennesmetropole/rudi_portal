package org.rudi.microservice.projekt.service.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.Role;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.common.service.helper.ResourceHelper;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.acl.helper.RolesHelper;
import org.rudi.facet.kmedia.bean.KindOfData;
import org.rudi.facet.kmedia.service.MediaService;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequest;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequestStatus;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.service.ProjectSpringBootTest;
import org.rudi.microservice.projekt.service.confidentiality.impl.ConfidentialityHelper;
import org.rudi.microservice.projekt.service.helper.MyInformationsHelper;
import org.rudi.microservice.projekt.service.mapper.ReutilisationStatusMapper;
import org.rudi.microservice.projekt.service.replacer.TransientDtoReplacerTest;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusDao;
import org.rudi.microservice.projekt.storage.dao.support.SupportDao;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleDao;
import org.rudi.microservice.projekt.storage.dao.type.ProjectTypeDao;
import org.rudi.microservice.projekt.storage.entity.ReutilisationStatusEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Class de test de la couche service
 */
@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProjectServiceUT {

	private static final KnownProject PROJET_LAMPADAIRES = new KnownProject("lampadaires",
			"Projet de comptage des lampadaires");
	private static final KnownProject PROJET_POUBELLES = new KnownProject("poubelles",
			"Projet de suivi des poubelles jaunes orangées");
	private static final String DEFAULT_LOGO_FILE_NAME = "media/default-logo.png";
	private static final String SECOND_LOGO_FILE_NAME = "media/project-logo-changed.png";

	private final ProjectService projectService;
	private final ProjectDao projectDao;
	private final TerritorialScaleDao territorialScaleDao;
	private final ProjectTypeDao projectTypeDao;
	private final SupportDao supportDao;
	private final ReutilisationStatusDao reutilisationStatusDao;

	private final JsonResourceReader jsonResourceReader;
	private final List<TransientDtoReplacerTest> transientDtoReplacers;

	private final ConfidentialityHelper confidentialityHelper;
	private final ReutilisationStatusMapper reutilisationStatusMapper;
	private final ResourceHelper resourceHelper;

	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private ACLHelper aclHelper;
	@MockBean
	private RolesHelper rolesHelper;
	@MockBean
	private MyInformationsHelper myInformationsHelper;
	@MockBean
	private MediaService mediaService;

	@SuppressWarnings("unused") // mocké pour ACLHelper
	@MockBean(name = "rudi_oauth2")
	private WebClientConfig webClientConfig;

	@SuppressWarnings("unused") // mocké pour OrganizationHelper
	@MockBean(name = "struktureWebClient")
	private WebClientConfig struktureWebClient;

	@BeforeEach
	void init() throws IOException {
		// Création des ReutilisationStatus nécessaires aux tests
		ReutilisationStatusEntity reutilisationStatus = reutilisationStatusMapper
				.dtoToEntity(jsonResourceReader.read("reutilisationstatus/project.json", ReutilisationStatus.class));
		if (reutilisationStatusDao.findByCode(reutilisationStatus.getCode()) == null) {
			reutilisationStatusDao.save(reutilisationStatus);
		}
	}

	@AfterEach
	void tearDown() {
		projectDao.deleteAll();

		projectTypeDao.deleteAll();
		supportDao.deleteAll();
		territorialScaleDao.deleteAll();
		reutilisationStatusDao.deleteAll();
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

	@Test
	void createProject() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		createdProject.setTargetAudiences(createdProject.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		assertThat(createdProject).as("On retrouve tous les champs du projet à créer").usingRecursiveComparison()
				.ignoringFields("uuid", "creationDate", "updatedDate").isEqualTo(projectToCreate);
	}

	@Test
	void createProjectWithoutAuthentication() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockUnauthenticatedUser();

		assertThatThrownBy(() -> projectService.createProject(projectToCreate))
				.isInstanceOf(AppServiceUnauthorizedException.class)
				.hasMessage("Cannot modify project list without authentication");
	}

	private void mockAuthenticatedUserToCreateProject(Project project) throws AppServiceUnauthorizedException {
		mockAuthenticatedUserFromManager(project.getOwnerUuid());
	}

	private void mockAuthenticatedUserFromManager(UUID managerUserUuid) throws AppServiceUnauthorizedException {
		final User user = new User().login("mpokora").uuid(managerUserUuid);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(aclHelper.getAuthenticatedUser()).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
	}

	private void mockAuthenticatedUserFromModerator(UUID moderatorUuid) {
		final User user = new User().login("PresentMic").uuid(moderatorUuid);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);
		when(rolesHelper.hasAnyRole(user, Role.MODERATOR)).thenReturn(true);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
	}

	private void mockAuthenticatedUserNotOwner(UUID managerUserUuid) {
		final User user = new User().login("shakira").uuid(managerUserUuid);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
	}

	private void mockUnauthenticatedUser() {
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(null);
	}

	@Test
	void createProjectWithoutProjectType() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		project.setType(null);
		createEntities(project);

		mockAuthenticatedUserToCreateProject(project);

		final Project createdProject = projectService.createProject(project);

		assertThat(createdProject.getType()).as("Le type est facultatif").isNull();
	}

	@Test
	void createProjectWithoutTerritorialScale() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		project.setTerritorialScale(null);
		createEntities(project);

		mockAuthenticatedUserToCreateProject(project);

		final Project createdProject = projectService.createProject(project);

		assertThat(createdProject.getTerritorialScale()).as("L'échelle de territoire est facultative").isNull();
	}

	@Test
	void createProjectWithoutDesiredSupports() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		project.setDesiredSupports(Collections.emptyList());
		createEntities(project);

		mockAuthenticatedUserToCreateProject(project);

		final Project createdProject = projectService.createProject(project);
		// On peut créer un projet initialement sans desireSupports
		assertThat(createdProject.getDesiredSupports()).isEmpty();
	}

	@Test
	@Disabled
	// FIXME ce test ne fonctionne que s'il n'est pas lancé avec maven
	void createProjectWithoutConfidentiality() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		project.setConfidentiality(null);
		createEntities(project);

		mockAuthenticatedUserToCreateProject(project);

		final Project createdProject = projectService.createProject(project);
		assertThat(createdProject).as("Le projet est créé avec la confidentialité par défaut")
				.hasFieldOrPropertyWithValue("confidentiality.uuid",
						confidentialityHelper.getDefaultConfidentiality().getUuid());
	}

	@Test
	void createProjectWithoutManager() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		mockAuthenticatedUserToCreateProject(project);
		project.setOwnerUuid(null);
		createEntities(project);

		assertThatThrownBy(() -> projectService.createProject(project)).isInstanceOf(MissingParameterException.class)
				.hasMessage("owner_uuid manquant");
	}

	@Test
	void createProjectWithProjectTypeWithoutUuid() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(project);
		project.getType().setUuid(null);

		mockAuthenticatedUserToCreateProject(project);

		assertThatThrownBy(() -> projectService.createProject(project)).isInstanceOf(MissingParameterException.class)
				.hasMessage("type.uuid manquant");
	}

	@Test
	void createProjectWithTerritorialScaleWithoutUuid() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(project);
		project.getTerritorialScale().setUuid(null);

		mockAuthenticatedUserToCreateProject(project);

		assertThatThrownBy(() -> projectService.createProject(project)).isInstanceOf(MissingParameterException.class)
				.hasMessage("territorial_scale.uuid manquant");
	}

	@Test
	void createProjectWithDesiredSupportsWithoutUuid() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(project);
		project.getDesiredSupports().get(0).setUuid(null);

		assertThatThrownBy(() -> projectService.createProject(project)).isInstanceOf(MissingParameterException.class)
				.hasMessage("support.uuid manquant");
	}

	@Test
	void createProjectWithConfidentialityWithoutUuid() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(project);
		project.getConfidentiality().setUuid(null);

		assertThatThrownBy(() -> projectService.createProject(project)).isInstanceOf(MissingParameterException.class)
				.hasMessage("confidentiality.uuid manquant");
	}

	@Test
	void createProjectWithInconsistentPeriod() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(project);

		// On inverse date de fin et début qui viennent du JSON pour créer une période incohérente
		LocalDateTime endDate = project.getExpectedCompletionEndDate();
		project.setExpectedCompletionEndDate(project.getExpectedCompletionStartDate());
		project.setExpectedCompletionStartDate(endDate);

		mockAuthenticatedUserToCreateProject(project);

		// On vérifie que ça pète bien
		assertThatThrownBy(() -> projectService.createProject(project))
				.isInstanceOf(AppServiceBadRequestException.class);
	}

	@Test
	void createProjectWithUuid() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		final UUID forcedUuid = UUID.randomUUID();
		projectToCreate.setUuid(forcedUuid);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		final Project createdProject = projectService.createProject(projectToCreate);

		assertThat(createdProject.getUuid())
				.as("Même si on indique un UUID à la création d'un projet, il n'est pas pris en compte mais regénéré")
				.isNotEqualTo(forcedUuid);
	}

	@Test
	@DisplayName("Je crée le projet pour quelqu'un d'autre")
	void createSomeoneElseSProject() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		val projectManager = new User().login("thebigboss").uuid(project.getOwnerUuid());
		when(aclHelper.getUserByUUID(projectManager.getUuid())).thenReturn(projectManager);
		createEntities(project);

		mockAuthenticatedUserNotOwner(UUID.randomUUID());

		assertThatThrownBy(() -> projectService.createProject(project))
				.as("Je ne peux pas créer le projet pour quelqu'un d'autre")
				.isInstanceOf(AppServiceForbiddenException.class).hasMessage(
						"Authenticated user must be moderator or must be the same user as existing project manager");
	}

	@Test
	void updateProject() throws IOException, AppServiceException {
		final Project project = createProject(PROJET_LAMPADAIRES);
		project.setTitle("Projet de comptage des lampadaires revu et corrigé");

		final Project updatedProject = projectService.updateProject(project);

		assertThat(updatedProject).as("Aucun champ n'a été modifié à part le titre").usingRecursiveComparison()
				.ignoringFields("datasetRequests", "creationDate", "updatedDate").isEqualTo(project);
	}

	@Test
	void updateProjectWithNullDatasetRequests() throws IOException, AppServiceException {
		final Project project = createProject(PROJET_LAMPADAIRES);
		final Project updatedProject = projectService.updateProject(project);

		assertThat(updatedProject).isNotNull().hasFieldOrPropertyWithValue("datasetRequests", Collections.emptyList());
	}

	@Test
	@DisplayName("Je modifie un projet qui n'existe pas")
	void updateUnexistingProject() throws IOException, AppServiceException {
		final Project project = createProject(PROJET_LAMPADAIRES);
		project.setUuid(UUID.randomUUID());

		assertThatThrownBy(() -> projectService.updateProject(project))
				.as("On ne peut pas modifier un projet inexistant").isInstanceOf(AppServiceNotFoundException.class)
				.hasMessage("project with UUID = \"%s\" not found", project.getUuid());
	}

	@Test
	@DisplayName("Je modifie le projet créé par quelqu'un d'autre")
	void updateSomeoneElseSProject() throws IOException, AppServiceException {
		final Project project = createProject(PROJET_LAMPADAIRES);
		project.setTitle("Projet de comptage des lampadaires revu et corrigé");

		final UUID otherManager = UUID.randomUUID();
		project.setOwnerUuid(otherManager);
		createEntities(project);
		mockAuthenticatedUserFromManager(otherManager);

		assertThatThrownBy(() -> projectService.updateProject(project))
				.as("Je ne peux pas modifier le projet créé par quelqu'un d'autre")
				.isInstanceOf(AppServiceForbiddenException.class).hasMessage(
						"Authenticated user must be moderator or must be the same user as existing project manager");
	}

	@Test
	@DisplayName("Je modifie le projet créé par quelqu'un d'autre en tant qu'animateur")
	void updateSomeoneElseSProjectAsModerator() throws IOException, AppServiceException {
		final Project project = createProject(PROJET_LAMPADAIRES);
		project.setTitle("Projet de comptage des lampadaires revu et corrigé");

		final UUID otherManager = UUID.randomUUID();
		project.setOwnerUuid(otherManager);
		createEntities(project);
		mockAuthenticatedUserFromModerator(otherManager);

		project.setTargetAudiences(project.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project updatedProject = projectService.updateProject(project);

		updatedProject.setTargetAudiences(updatedProject.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		assertThat(updatedProject).as("Aucun champ n'a été modifié à part le titre").usingRecursiveComparison()
				.ignoringFields("datasetRequests", "creationDate", "updatedDate").isEqualTo(project);
	}

	@Test
	void searchProjectThemes() throws IOException, AppServiceException {

		createProject(PROJET_LAMPADAIRES);
		createProject(PROJET_POUBELLES);

		val pageable = PageRequest.of(0, 2);
		final Page<Project> projects = projectService
				.searchProjects(new ProjectSearchCriteria().themes(Arrays.asList("comptage", "lampadaires")), pageable);

		assertThat(projects).as("On retrouve uniquement le project attendu").extracting("title")
				.containsExactly(PROJET_LAMPADAIRES.getTitle());
	}

	@Test
	void searchProjectKeywords() throws IOException, AppServiceException {

		createProject(PROJET_LAMPADAIRES);
		createProject(PROJET_POUBELLES);

		val pageable = PageRequest.of(0, 2);
		final Page<Project> projects = projectService.searchProjects(
				new ProjectSearchCriteria().keywords(Arrays.asList("comptage", "lampadaires")), pageable);

		assertThat(projects).as("On retrouve uniquement le project attendu").extracting("title")
				.containsExactly(PROJET_LAMPADAIRES.getTitle());
	}

	@Test
	void deleteProject() throws IOException, AppServiceException {
		final long totalElementsBeforeCreate = countProjects();

		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final long totalElementsAfterCreate = countProjects();
		assertThat(totalElementsAfterCreate).as("Le projet est bien créé").isEqualTo(totalElementsBeforeCreate + 1);

		projectService.deleteProject(createdProject.getUuid());
		final long totalElementsAfterDelete = countProjects();
		assertThat(totalElementsAfterDelete).as("Le projet est bien supprimé").isEqualTo(totalElementsBeforeCreate);
	}

	@Test
	@DisplayName("Je supprime un projet qui n'existe pas")
	void deleteUnexistingProject() throws IOException, AppServiceException {
		final long totalElementsBeforeCreate = countProjects();

		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		projectService.deleteProject(createdProject.getUuid());
		projectService.deleteProject(createdProject.getUuid());

		final long totalElementsAfterSecondDelete = countProjects();
		assertThat(totalElementsAfterSecondDelete).as("Aucun projet n'a été de nouveau supprimé")
				.isEqualTo(totalElementsBeforeCreate);
	}

	@Test
	@DisplayName("Je supprime le projet créé par quelqu'un d'autre")
	void deleteSomeoneElseSProject() throws IOException, AppServiceException {
		final long totalElementsBeforeCreate = countProjects();

		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final long totalElementsAfterCreate = countProjects();
		assertThat(totalElementsAfterCreate).as("Le projet est bien créé").isEqualTo(totalElementsBeforeCreate + 1);

		mockAuthenticatedUserNotOwner(UUID.randomUUID());

		assertThatThrownBy(() -> projectService.deleteProject(createdProject.getUuid()))
				.as("Je ne peux pas supprimer le projet créé par quelqu'un d'autre")
				.isInstanceOf(AppServiceForbiddenException.class).hasMessage(
						"Authenticated user must be moderator or must be the same user as existing project manager");

		final long totalElementsAfterDelete = countProjects();
		assertThat(totalElementsAfterDelete).as("Aucun projet n'a été supprimé")
				.isEqualTo(totalElementsBeforeCreate + 1);
	}

	@Test
	@DisplayName("Je supprime le projet créé par quelqu'un d'autre en tant qu'animateur")
	void deleteSomeoneElseSProjectAsModerator() throws IOException, AppServiceException {
		final long totalElementsBeforeCreate = countProjects();

		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final long totalElementsAfterCreate = countProjects();
		assertThat(totalElementsAfterCreate).as("Le projet est bien créé").isEqualTo(totalElementsBeforeCreate + 1);

		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromModerator(otherManager);

		projectService.deleteProject(createdProject.getUuid());

		final long totalElementsAfterDelete = countProjects();
		assertThat(totalElementsAfterDelete).as("Le projet est bien supprimé").isEqualTo(totalElementsBeforeCreate);
	}

	@Test
	@DisplayName("Modification du champ ReutilsiationStatus d'un projet via UpdateProject")
	void updateProjectFieldReutilisationStatus() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);

		// Récupération puis ajout en BDD du status pour changement
		ReutilisationStatus status = jsonResourceReader.read("reutilisationstatus/reuse.json",
				ReutilisationStatus.class);
		ReutilisationStatusEntity reutilisationStatus = reutilisationStatusMapper.dtoToEntity(status);
		if (reutilisationStatusDao.findByCode(reutilisationStatus.getCode()) == null) {
			reutilisationStatusDao.save(reutilisationStatus);
		}

		createdProject.setReutilisationStatus(status);
		val updatedProject = projectService.updateProject(createdProject);

		assertThat(updatedProject.getReutilisationStatus().getCode()).isEqualTo("REUSE")
				.as("Le code du Reutilsiation Status à ce stade doit être REUSE");

	}

	@Test
	@DisplayName("Vérifie que l'utilisateur connecté est owner du projet - passant")
	void authenticatedUserIsProjectOwner() throws Exception {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		final UUID projectUuid = createdProject.getUuid();

		// On fait en sorte que le helper renvoit un utilisateur connecté
		when(myInformationsHelper.getMeAndMyOrganizationUuids()).thenReturn(List.of(projectToCreate.getOwnerUuid()));

		assertThat(projectService.isAuthenticatedUserProjectOwner(projectUuid))
				.as("L'utilisateur connecté est celui qui a créé le projet, il doit donc avoir accès.").isTrue();
	}

	@Test
	@DisplayName("Vérifie que l'utilisateur connecté est owner du projet - non passant")
	void authenticatedUserIsNotProjectOwner() throws Exception {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		final UUID projectUuid = createdProject.getUuid();

		// on fait en sorte que le helper renvoit un autre utilisateur que celui connecté
		when(myInformationsHelper.getMeAndMyOrganizationUuids()).thenReturn(List.of(UUID.randomUUID()));

		assertThat(projectService.isAuthenticatedUserProjectOwner(projectUuid))
				.as("L'utilisateur connecté n'est pas celui qui a créé le projet, il ne doit donc pas avoir accès.")
				.isFalse();
	}

	@Test
	@DisplayName("Vérifie que l'utilisateur connecté est owner du projet - projectUuid ne renvoi aucun projet")
	void authenticatedUserISProjectOwnerNoProjectFound() throws Exception {
		// On ne crée pas de projet
		final UUID projectUuid = UUID.randomUUID();

		// on fait en sorte que le helper renvoit un autre utilisateur que celui connecté
		when(myInformationsHelper.getMeAndMyOrganizationUuids()).thenReturn(List.of(UUID.randomUUID()));

		// Test random UUID (ne renvoit aucun projet)
		assertThrows(AppServiceNotFoundException.class,
				() -> projectService.isAuthenticatedUserProjectOwner(projectUuid));
		// Test UUID du projet est null
		assertThrows(AppServiceNotFoundException.class, () -> projectService.isAuthenticatedUserProjectOwner(null));
	}

	@Test
	@DisplayName("Crée un NewDatasetRequest liée à un projet - Authorized")
	void createNewDatasetRequest() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		// Création de la newDatasetRequest à lier au projet
		final NewDatasetRequest request = getNewDatasetRequest();

		// Ajout de la newDatasetRequest au projet
		val requestCreated = projectService.createNewDatasetRequest(projectUuid, request);

		assertThat(requestCreated.getTitle()).as("Les champs doivent être égaux - Title").isEqualTo(request.getTitle());
		assertThat(requestCreated.getDescription()).as("Les champs doivent être égaux - Description")
				.isEqualTo(request.getDescription());
		assertThat(requestCreated.getStatus().getValue()).as("Le status doit être renseignée")
				.isEqualTo(NewDatasetRequestStatus.DRAFT.getValue());
		val now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
		assertThat(requestCreated.getCreationDate().truncatedTo(ChronoUnit.MINUTES))
				.as("La demande doit avoir été créée maintenant").isEqualTo(now);

		val newDatasetRequestsFromProject = projectService.getNewDatasetRequests(projectUuid);
		boolean isContained = false;
		for (NewDatasetRequest r : newDatasetRequestsFromProject) {
			if (r.getUuid().equals(requestCreated.getUuid())) {
				isContained = true;
				break;
			}
		}
		assertThat(isContained)
				.as("La liste des NewDatasetRequest du projet doit contenir la NewDatasetRequest créée précédemment.")
				.isTrue();
	}

	@Test
	@DisplayName("Crée une newDatasetRequest et tente de la lier à un projet dont on est pas le owner")
	void createNewDatasetRequestNotAuthorized() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		// Création de la newDatasetRequest à lier au projet
		final NewDatasetRequest request = getNewDatasetRequest();

		// Changement de personne connectée
		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromManager(otherManager);

		assertThrows(AppServiceForbiddenException.class,
				() -> projectService.createNewDatasetRequest(projectUuid, request));
	}

	@Test
	@DisplayName("Modification d'un newDatasetRequest - Authorized")
	void updateNewDatasetRequest() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		// Création de la newDatasetRequest à lier au projet
		final NewDatasetRequest requestToCreate = getNewDatasetRequest();

		// Ajout de la newDatasetRequest au projet
		val requestCreated = projectService.createNewDatasetRequest(projectUuid, requestToCreate);

		// On vérifie que le champ que l'on va modifier est bien égal au départ (ndt : la création s'est bien passée)
		assertThat(requestCreated.getDescription()).as("Les champs doivent être égaux - Description")
				.isEqualTo(requestToCreate.getDescription());

		// Création d'un nouvel objet pour l'update avec les champs obligatoires
		val requestToUpdate = new NewDatasetRequest();
		requestToUpdate.setUuid(requestCreated.getUuid());
		requestToUpdate.setDescription("A new description for a newDatasetRequest");
		requestToUpdate.setTitle(requestCreated.getTitle());
		requestToUpdate.setUpdator(createdProject.getInitiator());
		requestToUpdate.setProcessDefinitionKey(requestCreated.getProcessDefinitionKey());
		requestToUpdate.setNewDatasetRequestStatus(requestCreated.getNewDatasetRequestStatus());

		val requestUpdated = projectService.updateNewDatasetRequest(projectUuid, requestToUpdate);

		// On vérifie que le champ que l'on a modifié est bien différent de ce qu'on avait
		assertThat(requestUpdated.getDescription()).as("Les champs ne doivent pas être égaux - Description")
				.isNotEqualTo(requestCreated.getDescription());
		// Mais que les autres champs n'ont pas changé
		assertThat(requestUpdated.getTitle()).as("Les champs non modifiés doivent rester intacte")
				.isEqualTo(requestCreated.getTitle());
	}

	@Test
	@DisplayName("Modification d'un newDatasetRequest d'un projet appartenant à quelqu'un d'autre")
	void updateNewDatasetRequestNotAuthorized() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		// Création de la newDatasetRequest à lier au projet
		final NewDatasetRequest requestToCreate = getNewDatasetRequest();

		// Ajout de la newDatasetRequest au projet
		val requestCreated = projectService.createNewDatasetRequest(projectUuid, requestToCreate);

		// On vérifie que le champ que l'on va modifier est bien égal au départ (ndt : la création s'est bien passée)
		assertThat(requestCreated.getDescription()).as("Les champs doivent être égaux - Description")
				.isEqualTo(requestToCreate.getDescription());

		// Création d'un nouvel objet pour l'update avec les champs obligatoires
		val requestToUpdate = new NewDatasetRequest();
		requestToUpdate.setUuid(requestCreated.getUuid());
		requestToUpdate.setDescription("A new description for a newDatasetRequest");
		requestToUpdate.setTitle(requestCreated.getTitle());
		requestToUpdate.setUpdator(createdProject.getInitiator());
		requestToUpdate.setProcessDefinitionKey(requestCreated.getProcessDefinitionKey());
		requestToUpdate.setNewDatasetRequestStatus(requestCreated.getNewDatasetRequestStatus());

		// Changement de personne connectée
		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromManager(otherManager);

		assertThrows(AppServiceForbiddenException.class,
				() -> projectService.updateNewDatasetRequest(projectUuid, requestToUpdate));
	}

	@Test
	@DisplayName("Suppression d'une newDatasetRequest - Authorized")
	void deleteNewDatasetRequest() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		// Création de la newDatasetRequest à lier au projet
		final NewDatasetRequest requestToCreate = getNewDatasetRequest();

		// Ajout de la newDatasetRequest au projet
		val requestCreated = projectService.createNewDatasetRequest(projectUuid, requestToCreate);

		val newDatasetRequestsFromProject = projectService.getNewDatasetRequests(projectUuid);
		boolean isContained = false;
		for (NewDatasetRequest r : newDatasetRequestsFromProject) {
			if (r.getUuid().equals(requestCreated.getUuid())) {
				isContained = true;
				break;
			}
		}
		assertThat(isContained)
				.as("La liste des NewDatasetRequest du projet doit contenir la NewDatasetRequest créée précédemment.")
				.isTrue();

		projectService.deleteNewDatasetRequest(projectUuid, requestCreated.getUuid());

		val newDatasetRequestsFromProjectAfterDelete = projectService.getNewDatasetRequests(projectUuid);
		boolean isStillContained = false;
		for (NewDatasetRequest r : newDatasetRequestsFromProjectAfterDelete) {
			if (r.getUuid().equals(requestCreated.getUuid())) {
				isStillContained = true;
				break;
			}
		}
		assertThat(isStillContained).as(
				"La liste des NewDatasetRequest du projet ne doit plus contenir la NewDatasetRequest créée précédemment.")
				.isFalse();
	}

	@Test
	@DisplayName("Suppression d'une newDatasetRequest d'un projet que je ne possède pas")
	void deleteNewDatasetRequestNotAuthorized() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		// Création de la newDatasetRequest à lier au projet
		final NewDatasetRequest requestToCreate = getNewDatasetRequest();

		// Ajout de la newDatasetRequest au projet
		val requestCreated = projectService.createNewDatasetRequest(projectUuid, requestToCreate);

		val newDatasetRequestsFromProject = projectService.getNewDatasetRequests(projectUuid);
		boolean isContained = false;
		for (NewDatasetRequest r : newDatasetRequestsFromProject) {
			if (r.getUuid().equals(requestCreated.getUuid())) {
				isContained = true;
				break;
			}
		}
		assertThat(isContained)
				.as("La liste des NewDatasetRequest du projet doit contenir la NewDatasetRequest créée précédemment.")
				.isTrue();

		// Changement de personne connectée
		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromManager(otherManager);

		assertThrows(AppServiceForbiddenException.class,
				() -> projectService.deleteNewDatasetRequest(projectUuid, requestCreated.getUuid()));

	}

	@Test
	@DisplayName("Ajout d'un media a un projet - Authorized and Not Authorized")
	void uploadProjectMedia() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		val logo = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(DEFAULT_LOGO_FILE_NAME);

		// Je suis connecté avec le porteur du projet, j'ai donc accès à la modification.
		assertAll(() -> projectService.uploadMedia(projectUuid, KindOfData.LOGO, logo));

		// Changement de personne connectée
		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromManager(otherManager);

		val newLogo = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(SECOND_LOGO_FILE_NAME);

		// Une exception est levée, car je ne suis pas connecté avec un utilisateur qui est Owner du projet
		assertThrows(AppServiceForbiddenException.class,
				() -> projectService.uploadMedia(projectUuid, KindOfData.LOGO, newLogo));
	}

	@Test
	@DisplayName("Ajout puis suppression d'un media a un projet - Authorized and Not Authorized")
	void deleteProjectMedia() throws AppServiceException, IOException {
		// Creation du projet
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));

		final Project createdProject = projectService.createProject(projectToCreate);
		// Récupération du projectUuid pour le lier avec la newDatasetRequest
		final UUID projectUuid = createdProject.getUuid();

		val logo = resourceHelper.getResourceFromAdditionalLocationOrFromClasspath(DEFAULT_LOGO_FILE_NAME);

		// Je suis connecté avec le porteur du projet, j'ai donc accès à la modification.
		assertAll(() -> projectService.uploadMedia(projectUuid, KindOfData.LOGO, logo));

		// Changement de personne connectée
		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromManager(otherManager);

		// Une exception est levée, car je ne suis pas connecté avec un utilisateur qui est Owner du projet
		assertThrows(AppServiceForbiddenException.class,
				() -> projectService.deleteMedia(projectUuid, KindOfData.LOGO));

		// Je me re connecte en tant qu'Owner du projet
		mockAuthenticatedUserToCreateProject(projectToCreate);
		assertAll(() -> projectService.deleteMedia(projectUuid, KindOfData.LOGO));
	}

	private NewDatasetRequest getNewDatasetRequest() {
		NewDatasetRequest request = new NewDatasetRequest();

		request.setTitle("Demande de données pour un test");
		request.setDescription("Besoin de nouvelles données pour un teste");

		return request;
	}

	private long countProjects() {
		val pageable = PageRequest.of(0, 100);
		return projectService.searchProjects(new ProjectSearchCriteria(), pageable).getTotalElements();
	}

	@Data
	private static class KnownProject {
		private final String file;
		private final String title;

		String getJsonPath() {
			return "projects/" + file + ".json";
		}
	}

}
