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
import org.mockito.InjectMocks;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.RoleCodes;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.Role;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.bpmn.helper.form.FormHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.rudi.facet.kaccess.bean.Organization;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.DatasetConfidentiality;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.core.bean.OwnerType;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.ProjectStatus;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.service.ProjectSpringBootTest;
import org.rudi.microservice.projekt.service.helper.MyInformationsHelper;
import org.rudi.microservice.projekt.service.replacer.TransientDtoReplacerTest;
import org.rudi.microservice.projekt.service.reutilisationstatus.ReutilisationStatusService;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Class de test de la couche service
 */
@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LinkedDatasetServiceUT {

	private static final KnownProject PROJET_LAMPADAIRES = new KnownProject("lampadaires",
			"Projet de comptage des lampadaires");

	private static final KnownProject PROJECT_IN_NAME_OF_ORGANIZATION = new KnownProject(
			"project_for_organization",
			"Projet d'une organisation donc créé par un membre d'organisation. La création par le membre ne fait pas de lui le porteur de projet");

	private static final KnownReutilisationStatus PROJECT = new KnownReutilisationStatus("project");
	private static final KnownReutilisationStatus REUSE = new KnownReutilisationStatus("reuse");

	private static final String COMMENTAIRE_KEY_MAP = "messageToProjectOwner";
	private static final String DATE_KEY_MAP = "commentDate";


	private final ProjectService projectService;

	private final JsonResourceReader jsonResourceReader;
	private final List<TransientDtoReplacerTest> transientDtoReplacers;
	private final LinkedDatasetDao linkedDatasetDao;
	private final ProjectDao projectDao;

	private final ReutilisationStatusDao reutilisationStatusDao;
	private final ReutilisationStatusService reutilisationStatusService;
	private final FormHelper formHelper;

	@InjectMocks
	private final LinkedDatasetService linkedDatasetService;

	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private ACLHelper aclHelper;
	@SuppressWarnings("unused") // mocké pour les tests via les orga
	@MockBean
	private OrganizationHelper organizationHelper;
	@MockBean
	private final ApplicationService applicationService;
	@SuppressWarnings("unused") // mocké pour ACLHelper
	@MockBean(name = "rudi_oauth2")
	private WebClientConfig webClientConfig;
	@SuppressWarnings("unused") // mocké pour OrganizationHelper
	@MockBean(name = "struktureWebClient")
	private WebClientConfig struktureWebClient;
	@MockBean
	private DatasetService datasetService;
	@MockBean
	private MyInformationsHelper myInformationsHelper;

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

	private ReutilisationStatus createReutilisationStatus(KnownReutilisationStatus reu) throws IOException {

		final ReutilisationStatus statusToCreate = jsonResourceReader.read(reu.getJsonPath(),
				ReutilisationStatus.class);
		return reutilisationStatusService.createReutilisationStatus(statusToCreate);
	}

	private void mockAuthenticatedUserToCreateProject(Project project) throws AppServiceUnauthorizedException, GetOrganizationMembersException {
		mockAuthenticatedUserFromManager(project.getOwnerUuid(), project.getOwnerType().equals(OwnerType.ORGANIZATION));
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

	@Test
	@DisplayName("Je crée un projet puis lui ajoute des JDD, l'ajout fonctionne.")
	void linkDatasetsToProject_DRAFT()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		assertEquals(ProjectStatus.DRAFT, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld_open = createLinkedDataset(null, "link opened", DatasetConfidentiality.OPENED);
		final var ld_restricted = createLinkedDataset(null, "link restricted", DatasetConfidentiality.RESTRICTED);
		ld_restricted.setEndDate(LocalDateTime.now().plusMonths(1L));

		final var ld_self = createLinkedDataset(null, "link selfdata", DatasetConfidentiality.SELFDATA);

		// On met une date de fin à ce JDD restreint même si il n'est pas sensé y'en avoir
		ld_open.setEndDate(LocalDateTime.now());

		// Ajout du JDD lié ouvert
		Metadata associated_open = createMetadataAssociated(ld_open);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_open);
		linkedDatasetService.linkProjectToDataset(projectUuid, ld_open);

		// Ajout du JDD lié restreint
		Metadata associated_restricted = createMetadataAssociated(ld_restricted);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_restricted);
		linkedDatasetService.linkProjectToDataset(projectUuid, ld_restricted);

		// Ajout du JDD lié selfdata
		Metadata associated_self = createMetadataAssociated(ld_self);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_self);
		linkedDatasetService.linkProjectToDataset(projectUuid, ld_self);

		// Check que le JDD lié ouvert est bien completé
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.hasSize(1);
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)).get(0))
				.hasFieldOrPropertyWithValue("linkedDatasetStatus", LinkedDatasetStatus.VALIDATED)
				.hasFieldOrPropertyWithValue("comment", "link opened");

		// et qu'il a bien aucune date de fin malgré l'alimentation initiale
		assertThat(
				linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)).get(0).getEndDate())
						.isNull();
	}

	@Test
	@DisplayName("Je crée un projet avec des JDD, puis les supprime, la suppression fonctionne.")
	void unlinkDatasetsToProject_DRAFT()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		assertEquals(ProjectStatus.DRAFT, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld_open = createLinkedDataset(null, "link opened", DatasetConfidentiality.OPENED);

		final var ld_restricted = createLinkedDataset(null, "link restricted", DatasetConfidentiality.RESTRICTED);
		ld_restricted.setEndDate(LocalDateTime.now().plusMonths(1L));

		final var ld_self = createLinkedDataset(null, "link selfdata", DatasetConfidentiality.SELFDATA);

		// Ajout du JDD lié ouvert
		Metadata associated_open = createMetadataAssociated(ld_open);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_open);
		var ld_openUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld_open).getUuid();

		// Ajout du JDD lié restreint
		Metadata associated_restricted = createMetadataAssociated(ld_restricted);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_restricted);
		var ld_restrictedUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld_restricted).getUuid();

		// Ajout du JDD lié selfdata
		Metadata associated_self = createMetadataAssociated(ld_self);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_self);
		final var ld_selfUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld_self).getUuid();

		// Check que les JDD ont bien été ajoutés
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_restrictedUuid)).isNotNull();
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_selfUuid)).isNotNull();

		doNothing().when(applicationService).deleteUserSubscriptionsForDatasetAPIs(any(String.class), any(UUID.class));
		linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_openUuid);
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNull();
		linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_restrictedUuid);
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_restrictedUuid)).isNull();
		linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_selfUuid);
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_selfUuid)).isNull();
	}

	@Test
	@DisplayName("Je crée un projet, l'annule puis lui ajoute un JDD ouvert, l'ajout est refusé")
	void linkOpenDatasetToProject_CANCELLED()
			throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		createdProject.setProjectStatus(ProjectStatus.CANCELLED);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.CANCELLED, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.linkProjectToDataset(projectUuid, ld1));

		// Check que le JDD n'a pas été ajouté
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isEmpty();
	}

	@Test
	@DisplayName("Je crée un projet avec un JDD ouvert, l'annule et supprime le JDD, la suppression est refusée")
	void unlinkOpenDatasetToProject_CANCELLED()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		var ld_openUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld1).getUuid();
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();

		// changement d'état du projet
		createdProject.setProjectStatus(ProjectStatus.CANCELLED);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.CANCELLED, createdProject.getProjectStatus());

		doNothing().when(applicationService).deleteUserSubscriptionsForDatasetAPIs(any(String.class), any(UUID.class));

		// tentative de suppression du JDD
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_openUuid));

		// Check que le JDD n'a pas été supprimé
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();
	}

	@Test
	@DisplayName("Je crée un projet, le refuse puis lui ajoute un JDD, l'ajout fonctionne.")
	void linkOpenDatasetToProject_REJECTED()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		createdProject.setProjectStatus(ProjectStatus.REJECTED);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.REJECTED, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// On met une date de fin à ce JDD restreint même si il n'est pas sensé y'en avoir
		ld1.setEndDate(LocalDateTime.now());

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		linkedDatasetService.linkProjectToDataset(projectUuid, ld1);

		// Check que le JDD lié ouvert est bien completé
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isNotEmpty();
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)).get(0))
				.hasFieldOrPropertyWithValue("linkedDatasetStatus", LinkedDatasetStatus.VALIDATED)
				.hasFieldOrPropertyWithValue("comment", "link opened");

		// et qu'il a bien aucune date de fin malgré l'alimentation initiale
		assertThat(
				linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)).get(0).getEndDate())
						.isNull();
	}

	@Test
	@DisplayName("Je crée un projet, le refuse puis lui ajoute un JDD et le supprime, la suppression fonctionne.")
	void unlinkOpenDatasetToProject_REJECTED()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		assertEquals(ProjectStatus.DRAFT, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld_open = createLinkedDataset(null, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated_open = createMetadataAssociated(ld_open);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_open);
		var ld_openUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld_open).getUuid();

		// Check que les JDD ont bien été ajoutés
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();

		// changement de statut du projet
		createdProject.setProjectStatus(ProjectStatus.REJECTED);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.REJECTED, createdProject.getProjectStatus());

		doNothing().when(applicationService).deleteUserSubscriptionsForDatasetAPIs(any(String.class), any(UUID.class));
		linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_openUuid);

		// le JDD a bien été supprimé
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNull();
	}

	@Test
	@DisplayName("Je crée un projet avec une réutilisation 'en cours', le valide  puis lui ajoute un JDD ouvert, l'ajout fonctionne")
	void linkOpenDatasetToProject_VALIDATED_INPROGRESS()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		createdProject.setProjectStatus(ProjectStatus.VALIDATED);
		createdProject.setReutilisationStatus(createReutilisationStatus(PROJECT));
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.VALIDATED, createdProject.getProjectStatus());
		assertEquals(Boolean.TRUE, createdProject.getReutilisationStatus().getDatasetSetModificationAllowed());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// On met une date de fin à ce JDD restreint même si il n'est pas sensé y'en avoir
		ld1.setEndDate(LocalDateTime.now());

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		linkedDatasetService.linkProjectToDataset(projectUuid, ld1);

		// Check que le JDD lié ouvert est bien completé
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isNotEmpty();
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)).get(0))
				.hasFieldOrPropertyWithValue("linkedDatasetStatus", LinkedDatasetStatus.VALIDATED)
				.hasFieldOrPropertyWithValue("comment", "link opened");
	}

	@Test
	@DisplayName("Je crée un projet avec un JDD ouvert, le valide à 'en cours' et supprime le JDD, la suppression fonctionne")
	void unlinkOpenDatasetToProject_VALIDATED_INPROGRESS()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		assertEquals(ProjectStatus.DRAFT, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld_open = createLinkedDataset(null, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated_open = createMetadataAssociated(ld_open);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated_open);
		var ld_openUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld_open).getUuid();

		// Check que les JDD ont bien été ajoutés
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();

		// changement de statut du projet
		createdProject.setProjectStatus(ProjectStatus.VALIDATED);
		createdProject.setReutilisationStatus(createReutilisationStatus(PROJECT));
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.VALIDATED, createdProject.getProjectStatus());
		assertEquals(Boolean.TRUE, createdProject.getReutilisationStatus().getDatasetSetModificationAllowed());

		doNothing().when(applicationService).deleteUserSubscriptionsForDatasetAPIs(any(String.class), any(UUID.class));
		linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_openUuid);

		// le JDD a bien été supprimé
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNull();
	}

	@Test
	@DisplayName("Je crée un projet avec une réutilisation 'finalisée', le valide puis lui ajoute un JDD ouvert, l'ajout est refusé")
	void linkOpenDatasetToProject_VALIDATED_FINISHED()
			throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		createdProject.setProjectStatus(ProjectStatus.VALIDATED);
		createdProject.setReutilisationStatus(createReutilisationStatus(REUSE));
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.VALIDATED, createdProject.getProjectStatus());
		assertEquals(Boolean.FALSE, createdProject.getReutilisationStatus().getDatasetSetModificationAllowed());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.linkProjectToDataset(projectUuid, ld1));

		// Check que le JDD n'a pas été ajouté
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isEmpty();
	}

	@Test
	@DisplayName("Je crée un projet avec un JDD ouvert, le valide à 'fini' et supprime le JDD, la suppression est refusée")
	void unlinkOpenDatasetToProject_VALIDATED_FINISHED()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		var ld_openUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld1).getUuid();
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();

		// changement d'état du projet
		createdProject.setProjectStatus(ProjectStatus.VALIDATED);
		createdProject.setReutilisationStatus(createReutilisationStatus(REUSE));
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.VALIDATED, createdProject.getProjectStatus());
		assertEquals(Boolean.FALSE, createdProject.getReutilisationStatus().getDatasetSetModificationAllowed());

		doNothing().when(applicationService).deleteUserSubscriptionsForDatasetAPIs(any(String.class), any(UUID.class));

		// tentative de suppression du JDD
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_openUuid));

		// Check que le JDD n'a pas été supprimé
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();
	}

	@Test
	@DisplayName("Je crée un projet, le soumet à validation puis lui ajoute un JDD ouvert, l'ajout est refusé")
	void linkOpenDatasetToProject_INPROGRESS()
			throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		createdProject.setProjectStatus(ProjectStatus.IN_PROGRESS);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.IN_PROGRESS, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.linkProjectToDataset(projectUuid, ld1));

		// Check que le JDD n'a pas été ajouté
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isEmpty();
	}

	@Test
	@DisplayName("Je crée un projet avec un JDD ouvert, le passe en validation et supprime le JDD, la suppression est refusée")
	void unlinkOpenDatasetToProject_INPROGRESS()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		var ld_openUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld1).getUuid();
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();

		// changement d'état du projet
		createdProject.setProjectStatus(ProjectStatus.IN_PROGRESS);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.IN_PROGRESS, createdProject.getProjectStatus());

		doNothing().when(applicationService).deleteUserSubscriptionsForDatasetAPIs(any(String.class), any(UUID.class));

		// tentative de suppression du JDD
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_openUuid));

		// Check que le JDD n'a pas été supprimé
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();
	}

	@Test
	@DisplayName("Je crée un projet, l'annule puis lui ajoute un JDD ouvert, l'ajout est refusé")
	void linkOpenDatasetToProject_DISENGAGED()
			throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		createdProject.setProjectStatus(ProjectStatus.DISENGAGED);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.DISENGAGED, createdProject.getProjectStatus());

		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1 = createLinkedDataset(null, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.linkProjectToDataset(projectUuid, ld1));

		// Check que le JDD n'a pas été ajouté
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isEmpty();
	}

	@Test
	@DisplayName("Je crée un projet avec un JDD ouvert, l'annule et supprime le JDD, la suppression est refusée")
	void unlinkOpenDatasetToProject_DISENGAGED()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// Ajout du JDD lié ouvert
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		var ld_openUuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld1).getUuid();
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();

		// changement d'état du projet
		createdProject.setProjectStatus(ProjectStatus.DISENGAGED);
		createdProject = projectService.updateProject(createdProject);
		assertEquals(ProjectStatus.DISENGAGED, createdProject.getProjectStatus());

		doNothing().when(applicationService).deleteUserSubscriptionsForDatasetAPIs(any(String.class), any(UUID.class));

		// tentative de suppression du JDD
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.unlinkProjectToDataset(projectUuid, ld_openUuid));

		// Check que le JDD n'a pas été supprimé
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld_openUuid)).isNotNull();
	}

	@Test
	@DisplayName("Je crée un projet puis lui ajoute un JDD restreint")
	void linkRestrictedDatasetToProject_DRAFT()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {

		// Création projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		assertEquals(ProjectStatus.DRAFT, createdProject.getProjectStatus());
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test
		final var ld1 = createLinkedDataset(null, "link restreint", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now());

		// Ajout du JDD lié restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		final var ld1Uuid = linkedDatasetService.linkProjectToDataset(projectUuid, ld1).getUuid();

		// Check que le JDD lié restreint est bien en cours
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, ld1Uuid)).isNotNull();
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.DRAFT))).isNotEmpty();
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.DRAFT)).get(0))
				.hasFieldOrPropertyWithValue("linkedDatasetStatus", LinkedDatasetStatus.DRAFT)
				.hasFieldOrPropertyWithValue("comment", "link restreint");

		// la date de fin est obligatoire pour une demande d'accès restreinte
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.DRAFT)).get(0).getEndDate())
				.isNotNull();
	}

	@Test
	@DisplayName("Je crée un projet puis lui ajoute un JDD restreint sans date de fin")
	void linkRestrictedDatasetToProjectWithoutEndDate() throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		for (LinkedDatasetStatus status : LinkedDatasetStatus.values()) {
			assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(status)))
					.as("À sa création, le projet n'utilise aucun JDD de statut " + status).isEmpty();
		}

		// Créations des JDDs de test sans date de fin
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restreint", DatasetConfidentiality.RESTRICTED);

		// Ajout du JDD lié restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);

		// On vérifie que ça pète bien en bad request quand il manque la date de fin
		assertThatExceptionOfType(AppServiceBadRequestException.class)
				.isThrownBy(() -> linkedDatasetService.linkProjectToDataset(projectUuid, ld1));
	}

	@Test
	@DisplayName("Je crée un projet, puis un autre tente d'y rajouter un JDD avec un utilisateur non autorisé")
	void linkOpenDatasetToProject_unauthorized()
			throws IOException, AppServiceException, DataverseAPIException {
		// Création du projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// On met une date de fin à ce JDD restreint même si il n'est pas sensé y'en avoir
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));

		// On se connecte avec quelqu'un d'autre.
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.USER));

		// Ajout du JDD lié restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.linkProjectToDataset(projectUuid, ld1));
	}

	@Test
	@DisplayName("Je crée un projet, puis un autre tente d'y supprimer un JDD ouvert avec un utilisateur non autorisé")
	void unlinkOpenDatasetToProject_unauthorized()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {
		// Création du projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// On met une date de fin à ce JDD restreint même si il n'est pas sensé y'en avoir
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));

		// Ajout du JDD lié restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getDatasetUuid();

		// Check que le JDD lié ouvert est bien completé
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isNotEmpty();

		// On se connecte avec quelqu'un d'autre.
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.USER));

		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.unlinkProjectToDataset(projectUuid, linkedDatasetUuid));
	}

	@Test
	@DisplayName("Je crée un projet, puis un autre tente d'y supprimer un JDD ouvert avec un utilisateur non autorisé")
	void updateLinkedDataset_unauthorized()
			throws IOException, AppServiceException, DataverseAPIException, APIManagerException {
		// Création du projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link opened", DatasetConfidentiality.OPENED);

		// On met une date de fin à ce JDD restreint même si il n'est pas sensé y'en avoir
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));

		// Ajout du JDD lié restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		LinkedDataset linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getDatasetUuid();

		// Check que le JDD lié ouvert est bien completé
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED))).isNotEmpty();

		// On se connecte avec quelqu'un d'autre.
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.USER));
		val newComment = "Shakira est dans la place !";
		linkedDataset.comment(newComment);

		assertThrows(AppServiceForbiddenException.class,
				() -> linkedDatasetService.updateLinkedDataset(projectUuid, linkedDataset));
		assertThat(linkedDatasetService.getLinkedDataset(projectUuid, linkedDatasetUuid)).isNotEqualTo(newComment);
	}

	@Test
	@DisplayName("Je récupère une décision en tant que Owner du projet, mais elle ne contient aucun commentaire")
	void getDecisionInformationsEmptyData() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();


		//Normalement le form retourné est null, car il ne contient aucune informations pour l'instant
		assertThat(linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid))
				.as("Le formulaire retourné doit être null, car aucune information n'a été saisie par que qui ce soit pour l'instant.")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décision en tant que Owner du projet qui contient commentaire et date")
	void getDecisionInformationsFullFilledData() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date, LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
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
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.DATE_KEY_MAP)).findFirst();
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

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute la date du jour
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
		assertThat(decision)
				.as("Le formulaire ne contenant pas de date doit envoyer null")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décision en tant que Owner du projet qui contient commentaire mais pas de date")
	void getDecisionInformationsOnlyComment() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
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
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));

		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.DATE_KEY_MAP)).findFirst();
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

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();


		assertThrows(AppServiceNotFoundException.class, ()-> linkedDatasetService.getDecisionInformations(UUID.randomUUID(), linkedDatasetUuid));
		assertThrows(AppServiceNotFoundException.class, ()-> linkedDatasetService.getDecisionInformations(projectUuid, UUID.randomUUID()));
		assertThrows(AppServiceNotFoundException.class, ()-> linkedDatasetService.getDecisionInformations(UUID.randomUUID(), UUID.randomUUID()));

	}

	@Test
	@DisplayName("Je tente de récupérer une décision, mais authentifié en tant qu'Administrateur")
	void getDecisionInformationAsAdministrator() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date, LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant qu'Administrateur
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.ADMINISTRATOR));

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section).as("La sectionj ne doit pas être null").isNotNull();
		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field date doit contenir la bonne date")
				.matches((f -> f.isPresent() && LocalDateTime.parse(f.get().getValues().get(0)).equals(date)));
	}

	@Test
	@DisplayName("Je tente de récupérer une décision, mais authentifié en tant que producteur")
	void getDecisionInformationAsProducer() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date, LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant que Provider
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.USER));

		// Création d'une fausse organization dans les metadata du linkedataset
		val fakeOrganization = new Organization();
		val fakeOrganizationUuid = UUID.randomUUID();
		fakeOrganization.setOrganizationId(fakeOrganizationUuid);
		val fakeMetadata = new Metadata();
		fakeMetadata.setProducer(fakeOrganization);
		when(datasetService.getDataset(linkedDataset.getDatasetUuid())).thenReturn(fakeMetadata);
		List<UUID> meAndMyOrganizations = List.of(aclHelper.getAuthenticatedUserUuid(),fakeOrganizationUuid);
		when(myInformationsHelper.getMeAndMyOrganizationUuids()).thenReturn(meAndMyOrganizations);

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
		assertThat(decision)
				.as("Le formulaire doit contenir le commentaire saisi précédemment.")
				.isNotNull();

		// Cette section doit contenir les champs commentaire et date
		val section = decision.getSections().stream().findFirst().isPresent() ? decision.getSections().stream().findFirst().get() : null;
		assertThat(section).as("La sectionj ne doit pas être null").isNotNull();
		//Test sur les contenu des map
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.DATE_KEY_MAP)).findFirst();
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

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date, LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant que Provider
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of(RoleCodes.USER));

		// Création d'une fausse organization dans les metadata du linkedataset
		val fakeOrganization = new Organization();
		val fakeOrganizationUuid = UUID.randomUUID();
		fakeOrganization.setOrganizationId(fakeOrganizationUuid);
		val fakeMetadata = new Metadata();
		fakeMetadata.setProducer(fakeOrganization);
		when(datasetService.getDataset(linkedDataset.getDatasetUuid())).thenReturn(fakeMetadata);

		// L'utilisateur n'est pas membre de l'organization liée au JDD.
		List<UUID> meAndMyOrganizations = List.of(aclHelper.getAuthenticatedUserUuid(),UUID.randomUUID());
		when(myInformationsHelper.getMeAndMyOrganizationUuids()).thenReturn(meAndMyOrganizations);

		// La fonction doit retourner une exception car l'utilisateur n'a pas les accès
		assertThrows(AppServiceUnauthorizedException.class, () -> linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid));

	}

	@Test
	@DisplayName("Je tente de récupérer une décision, mais authentifié en tant qu'utilisateur extérieur au projet sans role")
	void getDecisionInformationAsOtherUserWithNoRole() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date, LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// On se connecte avec quelqu'un d'autre.
		// Authentification en tant que Provider
		mockAuthenticatedUserOtherUser(UUID.randomUUID(), List.of());

		// La fonction doit retourner une exception car l'utilisateur n'a pas les accès
		assertThrows(AppServiceUnauthorizedException.class, () -> linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid));

	}

	@Test
	@DisplayName("Je récupère une décision en tant que membre d'une organisation owner du projet, mais elle ne contient aucun commentaire")
	void getDecisionInformationsEmptyDataForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();


		//Normalement le form retourné est null, car il ne contient aucune informations pour l'instant
		assertThat(linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid))
				.as("Le formulaire retourné doit être null, car aucune information n'a été saisie par que qui ce soit pour l'instant.")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décision en tant que membre d'une organisation owner du projet qui contient commentaire et date")
	void getDecisionInformationsFullFilledDataForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();


		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date, LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
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
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));
		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field date doit contenir la bonne date")
				.matches((f -> f.isPresent() && LocalDateTime.parse(f.get().getValues().get(0)).equals(date)));
	}

	@Test
	@DisplayName("Je récupère une décision en tant que membre d'une organisation owner du projet qui ne contient qu'une date, donc le résultat est null")
	void getDecisionInformationsOnlyDateForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute la date du jour
		val date = LocalDateTime.now();
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.DATE_KEY_MAP, date);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
		assertThat(decision)
				.as("Le formulaire ne contenant pas de date doit envoyer null")
				.isNull();
	}

	@Test
	@DisplayName("Je récupère une décision en tant que membre d'une organisation owner du projet qui contient commentaire mais pas de date")
	void getDecisionInformationsOnlyCommentForOrganization() throws Exception {
		//Create project
		final Project createdProject = createProject(PROJECT_IN_NAME_OF_ORGANIZATION);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, List.of(LinkedDatasetStatus.VALIDATED)))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restricted", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now().plusMonths(1));


		// Ajout du JDD lié Restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		val linkedDataset = linkedDatasetService.linkProjectToDataset(projectUuid, ld1);
		UUID linkedDatasetUuid = linkedDataset.getUuid();

		LinkedDatasetEntity linkedDatasetEntity = linkedDatasetDao.findByUuid(linkedDatasetUuid);
		assertThat(linkedDatasetEntity).as("L'entity doit avoir été créée").isNotNull();
		val dataNull = linkedDatasetEntity.getData();
		assertThat(dataNull).as("On ne doit avoir aucune data à ce stade").isNull();

		// On rajoute le commentaire à la date du jour
		val commentaire = "Ce formulaire contient bien un commentaire, la preuve !";
		Map<String, Object> data = Map.of(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP, commentaire);
		linkedDatasetEntity.setData(formHelper.deshydrateData(data));
		linkedDatasetDao.save(linkedDatasetEntity);

		// Le forumlaire retourné ne doit pas être null
		val decision = linkedDatasetService.getDecisionInformations(projectUuid, linkedDatasetUuid);
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
		val commentaireField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.COMMENTAIRE_KEY_MAP)).findFirst();
		assertThat(commentaireField)
				.as("Le field commentaire doit être renseigné")
				.matches(Optional::isPresent)
				.as("Le field commentaire doit contenir le bon commentaire")
				.matches(f -> f.isPresent() && f.get().getValues().get(0).equals(commentaire));

		val dateField = section.getFields().stream().filter(field -> field.getDefinition().getName().equals(LinkedDatasetServiceUT.DATE_KEY_MAP)).findFirst();
		assertThat(dateField)
				.as("Le field date est présent")
				.matches(Optional::isPresent)
				.as("Le field est présent, mais il doit être null")
				.matches(f -> f.isPresent() && f.get().getValues() == null);
	}


	@Data
	private static class KnownProject {
		private final String file;
		private final String title;

		String getJsonPath() {
			return "projects/" + file + ".json";
		}
	}

	@Data
	private static class KnownReutilisationStatus {
		private final String code;
		private UUID uuid;

		String getJsonPath() {
			return "reutilisationstatus/" + code + ".json";
		}
	}

	private LinkedDataset createLinkedDataset(UUID uuid, String comment, DatasetConfidentiality dc) {
		LinkedDataset linkedDataset = new LinkedDataset();
		linkedDataset.setComment(comment);
		linkedDataset.setUuid(uuid);
		linkedDataset.setDatasetConfidentiality(dc);
		linkedDataset.setDatasetUuid(UUID.randomUUID());
		return linkedDataset;
	}

	private Metadata createMetadataAssociated(LinkedDataset linkedDataset) {
		Metadata returned = new Metadata();
		returned.setResourceTitle("On s'en moque");
		MetadataAccessCondition accessCondition = new MetadataAccessCondition();
		MetadataAccessConditionConfidentiality confidentiality = new MetadataAccessConditionConfidentiality();
		confidentiality.setGdprSensitive(linkedDataset.getDatasetConfidentiality() == DatasetConfidentiality.SELFDATA);

		confidentiality
				.setRestrictedAccess(linkedDataset.getDatasetConfidentiality() == DatasetConfidentiality.RESTRICTED
						|| linkedDataset.getDatasetConfidentiality() == DatasetConfidentiality.SELFDATA);
		accessCondition.setConfidentiality(confidentiality);
		returned.setAccessCondition(accessCondition);
		return returned;
	}

	@AfterEach
	void tearDown() {
		linkedDatasetDao.deleteAll();
		projectDao.deleteAll();
		reutilisationStatusDao.deleteAll();
	}
}
