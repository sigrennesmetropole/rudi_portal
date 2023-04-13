package org.rudi.microservice.konsent.service.consent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.core.security.AuthenticatedUser;
import org.rudi.common.core.security.UserType;
import org.rudi.common.facade.util.UtilPageable;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.exception.BusinessException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.ACLHelper;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.facet.organization.helper.exceptions.GetOrganizationException;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentList;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentVersionList;
import org.rudi.microservice.konsent.core.bean.TargetType;
import org.rudi.microservice.konsent.core.bean.Treatment;
import org.rudi.microservice.konsent.core.bean.TreatmentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.TreatmentStatus;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.core.bean.TreatmentVersionSearchCriteria;
import org.rudi.microservice.konsent.service.KonsentSpringBootTest;
import org.rudi.microservice.konsent.service.consent.replacer.TransientDtoReplacer;
import org.rudi.microservice.konsent.service.treatment.TreatmentsService;
import org.rudi.microservice.konsent.storage.dao.treatment.TreatmentsDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.TreatmentVersionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@KonsentSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TreatmentsServiceTest {

	private static final String JSON_BASIC_TREATMENT = "treatment/basic-treatment.json";
	private static final String JSON_BASIC_TREATMENT_WITH_UUID = "treatment/basic-treatment-with-uuid.json";
	private static final String JSON_RESEARCH_TREATMENT = "treatment/research-treatment.json";
	private static final String DRAFT_TREATMENT_NAME = "Traitement non validé";
	private static final String VALIDATED_TREATMENT_NAME = "Traitement validé";
	private static final UUID TREATMENT_OWNER_UUID = UUID.fromString("b396a7ba-ac93-4e2f-bd19-1b63a82dad02");
	private static final int TREATMENT_VERSION_ONE = 1;
	private static final int TREATMENT_VERSION_TWO = 2;
	private final TreatmentsService treatmentsService;
	private final JsonResourceReader jsonResourceReader;
	private static final int PAGINATION_SIZE = 10;
	private final UtilPageable utilPageable = new UtilPageable(PAGINATION_SIZE);
	private final List<TransientDtoReplacer> transientDtoReplacers;
	private final TreatmentsDao treatmentsDao;
	private final TreatmentVersionDao treatmentVersionDao;

	@MockBean
	private UtilContextHelper utilContextHelper;

	@MockBean
	private ACLHelper aclHelper;

	@MockBean
	private OrganizationHelper organizationHelper;

	private User authenticatedUserToUser(AuthenticatedUser originalUser, UUID userUuid) {
		User user = new User();
		user.setFirstname(originalUser.getFirstname());
		user.setLastname(originalUser.getLastname());
		user.setLogin(originalUser.getLogin());
		user.setUuid(userUuid);
		user.setType(org.rudi.facet.acl.bean.UserType.PERSON);
		return user;
	}

	private void setConnectedUser(AuthenticatedUser authenticatedUser, UUID userUuid, List<UUID> organizationUuids)
			throws GetOrganizationException, AppServiceUnauthorizedException {
		User user = null;

		if (authenticatedUser != null) {
			user = authenticatedUserToUser(authenticatedUser, userUuid);
		}
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);
		when(aclHelper.getAuthenticatedUserUuid()).thenReturn(userUuid);
		when(aclHelper.getAuthenticatedUser()).thenReturn(user);

		if (authenticatedUser != null) {
			when(aclHelper.getUserByLogin(authenticatedUser.getLogin())).thenReturn(user);
		}

		when(organizationHelper.getMyOrganizationsUuids(userUuid)).thenReturn(organizationUuids);
	}

	private AuthenticatedUser getKnownUser() {
		return getUserWithLogin("valid@mail.com");
	}

	private AuthenticatedUser getUserWithLogin(String login) {
		AuthenticatedUser user = new AuthenticatedUser();
		user.setLogin(login);
		user.setFirstname("firstname");
		user.setLastname("lastname");
		user.setType(UserType.PERSON);
		user.setRoles(List.of("USER"));
		return user;
	}

	@AfterEach
	void tearDown() {
		treatmentVersionDao.deleteAll();
		treatmentsDao.deleteAll();
	}

	/**
	 * @param jsonPath chemin du fichier JSON
	 * @return Treatment entity créée en base
	 * @throws Exception
	 */
	private Treatment createTreatmentFromJson(String jsonPath) throws Exception {
		val treatmentDto = jsonResourceReader.read(jsonPath, Treatment.class);
		createEntities(treatmentDto.getVersion());
		return treatmentsService.createTreatment(treatmentDto);
	}

	/**
	 * Crée les entiités ou récupère celles qui étaient déjà en BD pour une Version
	 *
	 * @param treatmentVersion une version de traitement
	 * @throws AppServiceException
	 */
	private void createEntities(TreatmentVersion treatmentVersion) throws AppServiceException {
		for (final TransientDtoReplacer getterOrCreator : transientDtoReplacers) {
			getterOrCreator.replaceDtoFor(treatmentVersion);
		}
	}

	@DisplayName("Crée un traitement en BD à partir d'un traitement DTO sans uuid")
	@Test
	void createTreatmentWithoutUuid() throws Exception {
		val createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		assertThat(createdTreatment.getUuid())
				.as("Même si on n'indique pas d'UUID à la création d'un traitement, il est automatiquement généré")
				.isNotNull();
	}

	@DisplayName("Crée un traitement en BD à partir d'un traitement DTO avec uuid")
	@Test
	void createTreatmentWithUuid() throws Exception {
		val createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT_WITH_UUID);
		assertThat(createdTreatment.getUuid())
				.as("Si on indique un UUID à la création d'un traitement, il est pas pris en compte, un nouveau est généré")
				.isNotEqualTo(UUID.fromString("6ad2b2f9-a4cf-4aad-8220-9b94d6720ee3"));
	}

	@DisplayName("Supprime un traitement si conditions de suppression remplies")
	@Test
	void deleteTreatment() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		final long totalElementsBeforeCreate = countTreatment();
		val createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		final long totalElementsAfterCreate = countTreatment();
		assertThat(totalElementsAfterCreate).as("Le traitement est bien créée").isEqualTo(totalElementsBeforeCreate + 1);

		treatmentsService.deleteTreatment(createdTreatment.getUuid());
		final long totalElementsAfterDelete = countTreatment();
		assertThat(totalElementsAfterDelete).as("Le traitement est bien supprimé").isEqualTo(totalElementsBeforeCreate);
	}

	@DisplayName("Refus de supprimer un traitement qui a une version publiée")
	@Test
	void deleteTreatmentDecline() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		final long totalElementsBeforeCreate = countTreatment();

		val createdTreatment = createTreatmentFromJson(JSON_RESEARCH_TREATMENT);
		final long totalElementsAfterCreate = countTreatment();
		assertThat(totalElementsAfterCreate).as("Le traitement est bien créée").isEqualTo(totalElementsBeforeCreate + 1);
		assertThrows(AppServiceUnauthorizedException.class, () -> treatmentsService.deleteTreatment(createdTreatment.getUuid()));
	}

	@DisplayName("Supprime une version d'un traitement selon conditions")
	@Test
	void deleteTreatmentVersion() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		val createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		treatmentsService.deleteTreatmentVersion(createdTreatment.getUuid(), createdTreatment.getVersion().getUuid());
		final Treatment gotTreatment = treatmentsService.getTreatment(createdTreatment.getUuid(), false);
		assertThat(gotTreatment).as("Le traitement recupéré n'a plus de version courante")
				.extracting("version")
				.isNull();
	}

	@DisplayName("Refuse la suppression d'une version d'un traitement car publié")
	@Test
	void deleteTreatmentVersionDecline() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		val createdTreatment = createTreatmentFromJson(JSON_RESEARCH_TREATMENT);
		assertThrows(BusinessException.class, () -> treatmentsService.deleteTreatmentVersion(createdTreatment.getUuid(), createdTreatment.getVersion().getUuid()));
	}

	@DisplayName("Crée un traitement en BD et le récupère ensuite")
	@Test
	void getTreatment() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		val createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		final Treatment gotTreatment = treatmentsService.getTreatment(createdTreatment.getUuid(), false);

		assertThat(gotTreatment)
				.as("On retrouve le traitement créé")
				.isEqualToIgnoringGivenFields(createdTreatment, "creationDate", "updatedDate", "version"); // On ignore les dates et la version qui contient des dates car MaJ par le système
	}

	@DisplayName("Publie la version courante d'un traitement")
	@Test
	void publishTreatment() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		val createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		treatmentsService.publishTreatment(createdTreatment.getUuid());
		final Treatment gotTreatment = treatmentsService.getTreatment(createdTreatment.getUuid(), true);

		assertThat(gotTreatment.getVersion()).as("Le statut de la version courante du traitement est VALIDATED")
				.extracting("status")
				.isEqualTo(TreatmentStatus.VALIDATED);
	}

	@DisplayName("MAJ les champs d'un traitement selon conditions")
	@Test
	void updateTreatment() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		val createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		createdTreatment.setTargetType(TargetType.DATASET);
		treatmentsService.updateTreatment(createdTreatment);
		final Treatment gotTreatment = treatmentsService.getTreatment(createdTreatment.getUuid(), false);

		assertThat(gotTreatment).as("Le champ targetType du traitement a été mis à jour")
				.extracting("targetType")
				.isEqualTo(TargetType.DATASET);
	}

	// TODO: La version n'est pas renvoyée à cause du mapper
	@DisplayName("Refus de la tentative de MAJ des champs d'un traitement car déjà publié")
	@Test
	void updateTreatmentDecline() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		Treatment createdTreatment = createTreatmentFromJson(JSON_RESEARCH_TREATMENT);
		createdTreatment.setTargetType(TargetType.DATASET);
		treatmentsService.updateTreatment(createdTreatment);
		final Treatment gotTreatment = treatmentsService.getTreatment(createdTreatment.getUuid(), false);
		assertThat(gotTreatment).as("Le champ targetType du traitement n'a pas été mis à jour car traitement déjà publié")
				.extracting("targetType")
				.isEqualTo(TargetType.PROJECT);
	}

	// TODO: La version n'est pas renvoyée à cause du mapper
	@DisplayName("Retourne les versions d'un traitement")
	@Test
	void getTreatmentVersions() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		Treatment createdTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		treatmentsService.publishTreatment(createdTreatment.getUuid());
		// Une fois la version publiée, toute tentative de modification entraine la création d'une nouvelle
		createdTreatment.getVersion().setVersion(TREATMENT_VERSION_TWO);
		treatmentsService.updateTreatment(createdTreatment);
		TreatmentVersionSearchCriteria searchCriteria = new TreatmentVersionSearchCriteria()
				.treatmentUuid(createdTreatment.getUuid());
		val pageable = utilPageable.getPageable(0, 100, "updatedDate");
		final PagedTreatmentVersionList treatmentVersions = treatmentsService
				.searchTreatmentVersions(searchCriteria, pageable);

		assertThat(treatmentVersions.getElements()).as("Les éléments ne contiennent que les versions du traitement")
				.extracting("version")
				.containsOnly(TREATMENT_VERSION_ONE, TREATMENT_VERSION_TWO);
	}

	@DisplayName("Renvoie les traitements au statut DRAFT")
	@Test
	void searchTreatmentsTest() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		createTreatmentFromJson(JSON_BASIC_TREATMENT);
		createTreatmentFromJson(JSON_RESEARCH_TREATMENT);
		List<TreatmentStatus> treatmentStatuses = List.of(TreatmentStatus.DRAFT);
		TreatmentSearchCriteria searchCriteria = new TreatmentSearchCriteria()
				.treatmentStatuses(treatmentStatuses);
		val pageable = utilPageable.getPageable(0, 100, "updatedDate");

		PagedTreatmentList treatmentList = treatmentsService.searchTreatments(searchCriteria, pageable);
		assertThat(treatmentList.getElements()).as("Seul le traitement avec un statut DRAFT est retourné par la recherche")
				.extracting("name")
				.containsOnly(DRAFT_TREATMENT_NAME);
	}

	@DisplayName("Renvoie tous les traitements")
	@Test
	void searchAllTreatmentsTest() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		createTreatmentFromJson(JSON_BASIC_TREATMENT);
		createTreatmentFromJson(JSON_RESEARCH_TREATMENT);
		List<TreatmentStatus> treatmentStatuses = List.of(
				TreatmentStatus.VALIDATED,
				TreatmentStatus.DRAFT
		);
		TreatmentSearchCriteria searchCriteria = new TreatmentSearchCriteria()
				.treatmentStatuses(treatmentStatuses);
		val pageable = utilPageable.getPageable(0, 100, "updatedDate");

		PagedTreatmentList treatmentList = treatmentsService.searchTreatments(searchCriteria, pageable);
		assertThat(treatmentList.getElements()).as("Tous les traitements sont retournés par la recherche")
				.extracting("name")
				.containsOnly(DRAFT_TREATMENT_NAME, VALIDATED_TREATMENT_NAME);
	}

	private Long countTreatment() throws Exception {
		List<TreatmentStatus> treatmentStatuses = new ArrayList<>();
		treatmentStatuses.add(TreatmentStatus.VALIDATED);
		treatmentStatuses.add(TreatmentStatus.DRAFT);
		TreatmentSearchCriteria searchCriteria = new TreatmentSearchCriteria()
				.treatmentStatuses(treatmentStatuses);
		val pageable = utilPageable.getPageable(0, 100, "updatedDate");

		return treatmentsService.searchTreatments(searchCriteria, pageable).getTotal();
	}
}
