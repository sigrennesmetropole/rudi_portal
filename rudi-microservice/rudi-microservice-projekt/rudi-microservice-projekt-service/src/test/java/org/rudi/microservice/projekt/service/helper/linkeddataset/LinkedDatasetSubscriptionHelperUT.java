package org.rudi.microservice.projekt.service.helper.linkeddataset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.Role;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.acl.helper.RolesHelper;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.apimaccess.service.ApplicationService;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.bean.MetadataAccessCondition;
import org.rudi.facet.kaccess.bean.MetadataAccessConditionConfidentiality;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.bean.OrganizationMember;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationMembersException;
import org.rudi.microservice.projekt.core.bean.DatasetConfidentiality;
import org.rudi.microservice.projekt.core.bean.LinkedDataset;
import org.rudi.microservice.projekt.core.bean.Project;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.service.ProjectSpringBootTest;
import org.rudi.microservice.projekt.service.mapper.LinkedDatasetMapper;
import org.rudi.microservice.projekt.service.mapper.ProjectMapper;
import org.rudi.microservice.projekt.service.project.LinkedDatasetService;
import org.rudi.microservice.projekt.service.project.ProjectService;
import org.rudi.microservice.projekt.service.replacer.TransientDtoReplacerTest;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetDao;
import org.rudi.microservice.projekt.storage.dao.project.ProjectDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class LinkedDatasetSubscriptionHelperUT {

	private static final String JSON_EXPIRED = "linkeddatasets/linked_dataset_expired.json";
	private static final String JSON_NOT_EXPIRED = "linkeddatasets/linked_dataset_not_expired.json";
	private static final String JSON_FOR_SAME_DATASET = "linkeddatasets/linked_dataset_for_same_dataset.json";

	private final LinkedDatasetDao linkedDatasetDao;
	private final ProjectDao projectDao;
	private final JsonResourceReader jsonResourceReader;
	private final LinkedDatasetSubscriptionHelper linkedDatasetSubscriptionHelper;

	private final ProjectService projectService;

	private final List<TransientDtoReplacerTest> transientDtoReplacers;
	private final ProjectMapper projectMapper;
	private final LinkedDatasetMapper linkedDatasetMapper;
	private final LinkedDatasetService linkedDatasetService;
	@MockBean
	private final DatasetService datasetService;

	private static final LinkedDatasetSubscriptionHelperUT.KnownProject ORGANIZATION_PROJECT = new LinkedDatasetSubscriptionHelperUT.KnownProject(
			"project_for_organization",
			"Projet d'une organisation donc créé par un membre d'organisation. La création par le membre ne fait pas de lui le porteur de projet");

	private static final LinkedDatasetSubscriptionHelperUT.KnownProject PROJET_LAMPADAIRES = new LinkedDatasetSubscriptionHelperUT.KnownProject(
			"lampadaires", "Projet de comptage des lampadaires");

	private static final LinkedDatasetSubscriptionHelperUT.KnownProject PROJET_LAMPADAIRES_DEUX = new LinkedDatasetSubscriptionHelperUT.KnownProject(
			"lampadaires_same_owner", "Projet de comptage des lampadaires deux");

	private static final LinkedDatasetSubscriptionHelperUT.KnownProject PROJET_POUBELLES = new LinkedDatasetSubscriptionHelperUT.KnownProject(
			"poubelles", "Projet de suivi des poubelles jaunes orangées");

	@MockBean
	private RolesHelper rolesHelper;
	@MockBean
	private UtilContextHelper utilContextHelper;
	@MockBean
	private ACLHelper aclHelper;
	@MockBean
	private OrganizationHelper organizationHelper;
	@MockBean
	private ApplicationService applicationService;

	private LinkedDatasetEntity createLinkedDatasetFromJson(String jsonPath) throws IOException {
		final LinkedDatasetEntity linkedDataset = jsonResourceReader.read(jsonPath, LinkedDatasetEntity.class);
		return linkedDatasetDao.save(linkedDataset);
	}

	@Data
	private static class KnownProject {
		private final String file;
		private final String title;

		String getJsonPath() {
			return "projects/" + file + ".json";
		}
	}

	private Project createProject(LinkedDatasetSubscriptionHelperUT.KnownProject knownProject)
			throws IOException, AppServiceException {
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

	private void mockAuthenticatedUserToCreateProject(Project project) throws GetOrganizationMembersException {
		mockAuthenticatedUserFromManager(project.getOwnerUuid());
	}

	private void mockAuthenticatedUserFromManager(UUID managerUserUuid) throws GetOrganizationMembersException {
		final User user = new User().login("mpokora").uuid(managerUserUuid);
		when(aclHelper.getUserByLogin(user.getLogin())).thenReturn(user);
		when(aclHelper.getUserByUUID(user.getUuid())).thenReturn(user);

		final AuthenticatedUser authenticatedUser = new AuthenticatedUser();
		authenticatedUser.setLogin(user.getLogin());
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

		// Mock organization checking members
		final OrganizationMember member = new OrganizationMember();
		member.setUserUuid(user.getUuid());
		member.setUuid(managerUserUuid);
		when(organizationHelper.organizationContainsUser(managerUserUuid, user.getUuid())).thenReturn(true);
		when(organizationHelper.getOrganizationMembers(managerUserUuid)).thenReturn(List.of(member));
		when(rolesHelper.hasAnyRole(user, Role.MODERATOR)).thenReturn(true);
	}

	@Test
	@DisplayName("Si je n'ai qu'une demande qui donne accès à ce JDD, alors quand elle expire, je perds l'accès")
	void hasAccessThroughAnotherRequest_no() throws IOException {
		val requestToOne = createLinkedDatasetFromJson(JSON_EXPIRED);
		val requestToTwo = createLinkedDatasetFromJson(JSON_NOT_EXPIRED);
		val linkedDatasets = linkedDatasetDao.findAll();

		assertThat(linkedDatasets.size()).isEqualTo(2);
		assertThat(requestToOne.getDatasetUuid()).isNotEqualByComparingTo(requestToTwo.getDatasetUuid());
		assertThat(linkedDatasetSubscriptionHelper.hasAccessThroughAnotherRequest(linkedDatasets,
				requestToOne.getDatasetUuid())).isFalse();
	}

	@Test
	@DisplayName("Il existe au moins une seconde demande qui donne accès au JDD qui est en rapport avec le linked dataset qu'on traite")
	void hasAccessThroughAnotherRequest_yes() throws IOException {
		val requestToOne = createLinkedDatasetFromJson(JSON_EXPIRED);
		createLinkedDatasetFromJson(JSON_NOT_EXPIRED);
		val requestToSame = createLinkedDatasetFromJson(JSON_FOR_SAME_DATASET);
		val linkedDatasets = linkedDatasetDao.findAll();

		assertThat(linkedDatasets.size()).isEqualTo(3);
		assertThat(requestToOne.getDatasetUuid()).isEqualByComparingTo(requestToSame.getDatasetUuid());
		assertThat(linkedDatasetSubscriptionHelper.hasAccessThroughAnotherRequest(linkedDatasets,
				requestToOne.getDatasetUuid())).isTrue();
	}

	@Test
	@DisplayName("Pour un projet de type USER, retourne le nom du user qui a créé le projet et donc la souscription accordée par cette demande lui sera associée")
	void getSubscriptionOwnerName_is_user() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(PROJET_POUBELLES.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));
		// Création du projet en BD
		val projectCreated = projectService.createProject(projectToCreate);
		val result = projectMapper.dtoToEntity(projectCreated);

		// Test de l'exactitude de l'ownerName
		val ownerName = linkedDatasetSubscriptionHelper.getSubscriptionOwnerName(result);
		assertThat(ownerName).isEqualTo("mpokora");
	}

	@Test
	@DisplayName("Pour un projet de type ORGANIZATION, retourne le login de l'organisation qui a initié le projet et donc la souscription accordée par cette demande sera associée à l'organisation et non l'utilisateur faisant l'action de création")
	void getSubscriptionOwnerName_is_organization() throws IOException, AppServiceException {
		final Project projectToCreate = jsonResourceReader.read(ORGANIZATION_PROJECT.getJsonPath(), Project.class);
		createEntities(projectToCreate);

		mockAuthenticatedUserToCreateProject(projectToCreate);

		projectToCreate.setTargetAudiences(projectToCreate.getTargetAudiences().stream()
				.sorted(Comparator.comparing(TargetAudience::getUuid)).collect(Collectors.toList()));
		// Création du projet en BD
		val projectCreated = projectService.createProject(projectToCreate);
		val result = projectMapper.dtoToEntity(projectCreated);

		// Test de l'exactitude de l'ownerName
		// 1) Ce n'est pas le user qui fait l'action de création
		val ownerName = linkedDatasetSubscriptionHelper.getSubscriptionOwnerName(result);
		assertThat(ownerName).isNotEqualTo("mpokora");

		// 2) le ownerName == ownerUuid du projet
		assertThat(UUID.fromString(ownerName)).isEqualTo(projectCreated.getOwnerUuid());
	}

	@Test
	@DisplayName("Teste le bon calcul de toutes les demandes d'un ownerUuid")
	void getOwnerAllRequests() throws IOException, AppServiceException, DataverseAPIException, APIManagerException {
		// Création de projet
		val lampadaire = createProject(PROJET_LAMPADAIRES);
		val lampadaireDeux = createProject(PROJET_LAMPADAIRES_DEUX);
		val poubelle = createProject(PROJET_POUBELLES);

		// force le mock pour l'appel de la vérification de l'authent pour les appels qui suivent
		mockAuthenticatedUserToCreateProject(lampadaire);

		// force le mock pour l'appel de la vérification de l'authent pour les appels suivants
		mockAuthenticatedUserToCreateProject(lampadaire);

		// Création de linkedDataset
		val linkedDatasetExpired = linkedDatasetMapper.entityToDto(createLinkedDatasetFromJson(JSON_EXPIRED));
		val linkedDatasetNotExpired = linkedDatasetMapper.entityToDto(createLinkedDatasetFromJson(JSON_NOT_EXPIRED));

		// Association projet - linkedDataset
		val metadata = createMetadataAssociated(linkedDatasetExpired);
		when(datasetService.getDataset(linkedDatasetExpired.getDatasetUuid())).thenReturn(metadata);
		when(datasetService.getDataset(linkedDatasetNotExpired.getDatasetUuid())).thenReturn(metadata);
		val linkExpired = linkedDatasetService.linkProjectToDataset(lampadaire.getUuid(), linkedDatasetExpired);
		val linkNotExpired = linkedDatasetService.linkProjectToDataset(lampadaireDeux.getUuid(),
				linkedDatasetNotExpired);

		// Appel méthode à tester
		val poubelleOwner = new ArrayList<LinkedDatasetEntity>();
		val lampadaireOwner = new ArrayList<LinkedDatasetEntity>();
		linkedDatasetSubscriptionHelper.getOwnerAllRequests(List.of(poubelle.getOwnerUuid()), 0, poubelleOwner);
		linkedDatasetSubscriptionHelper.getOwnerAllRequests(List.of(lampadaire.getOwnerUuid()), 0, lampadaireOwner);

		// Tests
		assertThat(poubelleOwner)
				.as("Aucun linkedDataset n'a été associé au projet Poubelle, donc son owner n'a aucun linkedDataset")
				.isEmpty();
		assertThat(lampadaireOwner.size()).as(
				"Les projets lampadaires et lampadaireDeux appartiennent au même owner, donc ce owner a 2 linkedDataset")
				.isEqualTo(2);
		assertThat(lampadaireOwner.stream().map(LinkedDatasetEntity::getUuid).collect(Collectors.toList()))
				.as("Les linkedDataset de notre Owner sont bien ceux qu'on a lié à ces projets")
				.containsExactly(linkExpired.getUuid(), linkNotExpired.getUuid());
	}

	private Metadata createMetadataAssociated(LinkedDataset linkedDataset) {
		Metadata returned = new Metadata();
		returned.setResourceTitle("On s'en moque");
		MetadataAccessCondition accessCondition = new MetadataAccessCondition();
		MetadataAccessConditionConfidentiality confidentiality = new MetadataAccessConditionConfidentiality();
		confidentiality.setGdprSensitive(false);
		confidentiality.setRestrictedAccess(
				linkedDataset.getDatasetConfidentiality().equals(DatasetConfidentiality.RESTRICTED));
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
