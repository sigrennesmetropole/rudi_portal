package org.rudi.microservice.projekt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.oauth2.config.WebClientConfig;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.projekt.core.bean.DatasetConfidentiality;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.service.project.LinkedDatasetService;
import org.rudi.microservice.projekt.service.project.ProjectService;
import org.rudi.microservice.projekt.service.replacer.TransientDtoReplacer;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Class de test de la couche service
 */
@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LinkedDatasetServiceTest {

	private static final KnownProject PROJET_LAMPADAIRES = new KnownProject("lampadaires",
			"Projet de comptage des lampadaires");

	private final ProjectService projectService;

	private final JsonResourceReader jsonResourceReader;
	private final List<TransientDtoReplacer> transientDtoReplacers;
	private final LinkedDatasetDao linkedDatasetDao;
	private final ProjectDao projectDao;

	@InjectMocks
	private final LinkedDatasetService linkedDatasetService;

	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private ACLHelper aclHelper;
	@MockBean
	private OrganizationHelper organizationHelper;

	@SuppressWarnings("unused") // mocké pour ACLHelper
	@MockBean(name = "rudi_oauth2")
	private WebClientConfig webClientConfig;

	@SuppressWarnings("unused") // mocké pour OrganizationHelper
	@MockBean(name = "struktureWebClient")
	private WebClientConfig struktureWebClient;

	@MockBean
	private DatasetService datasetService;

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

	@Test
	@DisplayName("Je crée un projet puis lui ajoute un JDD ouvert")
	void linkOpenDatasetToProject() throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.VALIDATED))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

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
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.VALIDATED)).isNotEmpty();
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.VALIDATED).get(0))
				.hasFieldOrPropertyWithValue("linkedDatasetStatus", LinkedDatasetStatus.VALIDATED)
				.hasFieldOrPropertyWithValue("comment", "link opened");

		// et qu'il a bien aucune date de fin malgré l'alimentation initiale
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.VALIDATED)
				.get(0).getEndDate())
				.isNull();
	}

	@Test
	@DisplayName("Je crée un projet puis lui ajoute un JDD restreint")
	void linkRestrictedDatasetToProject() throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.VALIDATED))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

		// Créations des JDDs de test
		final var ld1Uuid = UUID.randomUUID();
		final var ld1 = createLinkedDataset(ld1Uuid, "link restreint", DatasetConfidentiality.RESTRICTED);

		// On met une date de fin à ce JDD restreint
		ld1.setEndDate(LocalDateTime.now());

		// Ajout du JDD lié restreint
		Metadata associated1 = createMetadataAssociated(ld1);
		when(datasetService.getDataset(any(UUID.class))).thenReturn(associated1);
		linkedDatasetService.linkProjectToDataset(projectUuid, ld1);

		// Check que le JDD lié restreint est bien en cours
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.DRAFT)).isNotEmpty();
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.DRAFT).get(0))
				.hasFieldOrPropertyWithValue("linkedDatasetStatus", LinkedDatasetStatus.DRAFT)
				.hasFieldOrPropertyWithValue("comment", "link restreint");

		// la date de fin est obligatoire pour une demande d'accès restreinte
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.DRAFT).get(0).getEndDate())
				.isNotNull();
	}

	@Test
	@DisplayName("Je crée un projet puis lui ajoute un JDD restreint sans date de fin")
	void linkRestrictedDatasetToProjectWithoutEndDate() throws IOException, AppServiceException, DataverseAPIException {

		// Création projet
		final Project createdProject = createProject(PROJET_LAMPADAIRES);
		final var projectUuid = createdProject.getUuid();

		// C'est bien vide
		assertThat(linkedDatasetService.getLinkedDatasets(projectUuid, LinkedDatasetStatus.VALIDATED))
				.as("À sa création, le projet n'utilise aucun JDD").isEmpty();

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

	@Data
	private static class KnownProject {
		private final String file;
		private final String title;

		String getJsonPath() {
			return "projects/" + file + ".json";
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
		confidentiality.setGdprSensitive(false);
		confidentiality.setRestrictedAccess(linkedDataset.getDatasetConfidentiality().equals(DatasetConfidentiality.RESTRICTED));
		accessCondition.setConfidentiality(confidentiality);
		returned.setAccessCondition(accessCondition);
		return returned;
	}

	@AfterEach
	void tearDown() {
		linkedDatasetDao.deleteAll();
		projectDao.deleteAll();
	}
}
