package org.rudi.microservice.projekt.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
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
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.acl.helper.RolesHelper;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.projekt.core.bean.DatasetConfidentiality;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ProjectSearchCriteria;
import org.rudi.microservice.projekt.service.confidentiality.impl.ConfidentialityHelper;
import org.rudi.microservice.projekt.service.project.ProjectService;
import org.rudi.microservice.projekt.service.replacer.TransientDtoReplacer;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.dao.support.SupportDao;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleDao;
import org.rudi.microservice.projekt.storage.dao.type.ProjectTypeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Class de test de la couche service
 */
@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ProjectServiceTest {

	private static final KnownProject PROJET_LAMPADAIRES = new KnownProject("lampadaires",
			"Projet de comptage des lampadaires");
	private static final KnownProject PROJET_POUBELLES = new KnownProject("poubelles",
			"Projet de suivi des poubelles jaunes orangées");

	private final ProjectService projectService;
	private final ProjectDao projectDao;
	private final TerritorialScaleDao territorialScaleDao;
	private final ProjectTypeDao projectTypeDao;
	private final SupportDao supportDao;

	private final JsonResourceReader jsonResourceReader;
	private final List<TransientDtoReplacer> transientDtoReplacers;

	private final ConfidentialityHelper confidentialityHelper;

	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private ACLHelper aclHelper;
	@MockBean
	private RolesHelper rolesHelper;

	@SuppressWarnings("unused") // mocké pour ACLHelper
	@MockBean(name = "rudi_oauth2")
	private WebClientConfig webClientConfig;

	@SuppressWarnings("unused") // mocké pour OrganizationHelper
	@MockBean(name = "struktureWebClient")
	private WebClientConfig struktureWebClient;

	@AfterEach
	void tearDown() {
		projectDao.deleteAll();

		projectTypeDao.deleteAll();
		supportDao.deleteAll();
		territorialScaleDao.deleteAll();
	}

	private Project createProject(KnownProject knownProject) throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(knownProject.getJsonPath(), Project.class);
		createEntities(project);

		mockAuthenticatedUserToCreateProject(project);

		return projectService.createProject(project);
	}

	private void createEntities(Project project) throws AppServiceException {

		for (final TransientDtoReplacer getterOrCreator : transientDtoReplacers) {
			getterOrCreator.replaceDtoFor(project);
		}

	}

	@Test
	void createProject() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		final Project createdProject = projectService.createProject(projectToCreate);
		assertThat(createdProject).as("On retrouve tous les champs du projet à créer")
				.isEqualToIgnoringGivenFields(projectToCreate, "uuid", "creationDate", "updatedDate");
	}

	@Test
	void createProjectWithoutAuthentication() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockUnauthenticatedUser();

		assertThatThrownBy(() -> projectService.createProject(projectToCreate)).isInstanceOf(
				AppServiceUnauthorizedException.class).hasMessage("Cannot modify project list without authentication");
	}

	private void mockAuthenticatedUserToCreateProject(Project project) {
		mockAuthenticatedUserFromManager(project.getOwnerUuid());
	}

	private void mockAuthenticatedUserFromManager(UUID managerUserUuid) {
		final User user = new User().login("mpokora").uuid(managerUserUuid);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);

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
		assertThatThrownBy(() -> projectService.createProject(project)).isInstanceOf(
				AppServiceBadRequestException.class);
	}

	@Test
	void createProjectWithUuid() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		final UUID forcedUuid = UUID.randomUUID();
		projectToCreate.setUuid(forcedUuid);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		final Project createdProject = projectService.createProject(projectToCreate);

		assertThat(createdProject.getUuid()).as(
						"Même si on indique un UUID à la création d'un projet, il n'est pas pris en compte mais regénéré")
				.isNotEqualTo(forcedUuid);
	}

	@Test
	@DisplayName("Je crée le projet pour quelqu'un d'autre")
	void createSomeoneElseSProject() throws IOException, AppServiceException {
		final Project project = jsonResourceReader.read(PROJET_LAMPADAIRES.getJsonPath(), Project.class);
		val projectManager = new User().login("thebigboss").uuid(project.getOwnerUuid());
		when(aclHelper.getUserByUUID(projectManager.getUuid())).thenReturn(projectManager);
		createEntities(project);

		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromManager(otherManager);

		assertThatThrownBy(() -> projectService.createProject(project)).as(
						"Je ne peux pas créer le projet pour quelqu'un d'autre")
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

		assertThatThrownBy(() -> projectService.updateProject(project)).as(
						"On ne peut pas modifier un projet inexistant").isInstanceOf(AppServiceNotFoundException.class)
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

		assertThatThrownBy(() -> projectService.updateProject(project)).as(
						"Je ne peux pas modifier le projet créé par quelqu'un d'autre")
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

		final Project updatedProject = projectService.updateProject(project);

		assertThat(updatedProject).as("Aucun champ n'a été modifié à part le titre").usingRecursiveComparison()
				.ignoringFields("datasetRequests", "creationDate", "updatedDate").isEqualTo(project);
	}

	@Test
	void searchProjectThemes() throws IOException, AppServiceException {

		createProject(PROJET_LAMPADAIRES);
		createProject(PROJET_POUBELLES);

		val pageable = PageRequest.of(0, 2);
		final Page<Project> projects = projectService.searchProjects(
				new ProjectSearchCriteria().themes(Arrays.asList("comptage", "lampadaires")), pageable);

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

		final UUID otherManager = UUID.randomUUID();
		mockAuthenticatedUserFromManager(otherManager);

		assertThatThrownBy(() -> projectService.deleteProject(createdProject.getUuid())).as(
						"Je ne peux pas supprimer le projet créé par quelqu'un d'autre")
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

	private LinkedDataset createLinkedDataset(UUID uuid, LinkedDatasetStatus status) {
		LinkedDataset linkedDataset = new LinkedDataset();
		linkedDataset.setLinkedDatasetStatus(status);
		linkedDataset.setComment("");
		linkedDataset.setUuid(UUID.fromString("ae6bf2e2-8410-443f-b05b-e4f05ee8d3ae"));
		linkedDataset.setDatasetConfidentiality(DatasetConfidentiality.OPENED);
		return linkedDataset;
	}
}
