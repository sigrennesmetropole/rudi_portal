package org.rudi.microservice.konsent.service.consent;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
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
import org.rudi.facet.buckets3.DocumentStorageService;
import org.rudi.facet.buckets3.exception.DocumentStorageException;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.konsent.core.bean.Consent;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.OwnerType;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.rudi.microservice.konsent.core.bean.Treatment;
import org.rudi.microservice.konsent.core.bean.TreatmentStatus;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.KonsentSpringBootTest;
import org.rudi.microservice.konsent.service.consent.replacer.TransientDtoReplacerTest;
import org.rudi.microservice.konsent.service.treatment.TreatmentsService;
import org.rudi.microservice.konsent.storage.dao.consent.ConsentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Class de test du ConsentService
 */
@KonsentSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsentServiceUT {

	@InjectMocks
	private final ConsentsService consentsService;
	@InjectMocks
	private final MyConsentsService myConsentsService;
	@InjectMocks
	private final TreatmentsService treatmentsService;

	private final List<TransientDtoReplacerTest> transientDtoReplacers;
	private final JsonResourceReader jsonResourceReader;
	private final UtilPageable utilPageable;
	private final ConsentDao consentDao;

	@MockBean
	private UtilContextHelper utilContextHelper;

	@MockBean
	private ACLHelper aclHelper;

	@MockBean
	private OrganizationHelper organizationHelper;

//	@MockBean
//	private PDFSigner pdfSigner;
//
//	@MockBean
//	private PDFConvertor pdfConvertor;

	@MockBean
	private DocumentStorageService documentStorageService;

	private static final OffsetDateTime expirationDate2 = OffsetDateTime.parse("2022-01-01T15:20:30+08:00");
	private static final UUID ownerUuid1 = UUID.randomUUID();
	private static final UUID ownerOrganisationUuid2 = UUID.randomUUID();
	private static final UUID ownerUuid3 = UUID.randomUUID();
	private static final String JSON_BASIC_TREATMENT = "treatment/basic-treatment.json";
	private static final String JSON_BASIC_TREATMENT_WITH_UUID = "treatment/basic-treatment-with-uuid.json";
	private static final String JSON_RESEARCH_TREATMENT = "treatment/research-treatment.json";
	private static final String OK_TREATMENT_4 = "treatment/ok-treatment4.json";
	private static final String OK_TREATMENT_5 = "treatment/ok-treatment5.json";
	private static final String NOK_TREATMENT = "treatment/nok-treatment.json";
	private static final String NOK_TREATMENT_2 = "treatment/nok-treatment2.json";
	private static final UUID TREATMENT_OWNER_UUID = UUID.fromString("b396a7ba-ac93-4e2f-bd19-1b63a82dad02");

	private UUID okTreatmentUuid;
	private UUID okTreatment2Uuid;
	private UUID okTreatment3Uuid;
	private UUID okTreatment4Uuid;
	private UUID okTreatmentVersionUuid;
	private UUID okTreatmentVersion2Uuid;
	private UUID okTreatmentVersion3Uuid;
	private UUID okTreatmentVersion4Uuid;
	private UUID okTreatmentVersion5Uuid;
	private UUID invalidVersionUuid;

	// CONFIGURATION DES TESTS

	@BeforeAll
	public void createTreatmentsInDatabase() throws Exception {
		createOkTreatments();
		createKoTreatments();
	}

	@AfterEach
	void tearDown() {
		consentDao.deleteAll();
	}

	/**
	 * @param jsonPath chemin du fichier JSON
	 * @return Treatment entity créée en base
	 * @throws Exception e
	 */
	private Treatment createTreatmentFromJson(String jsonPath) throws Exception {
		val treatmentDto = jsonResourceReader.read(jsonPath, Treatment.class);
		if (treatmentDto.getVersion() != null) {
			createEntities(treatmentDto.getVersion());
		}
		return treatmentsService.createTreatment(treatmentDto);
	}

	/**
	 * Crée les entiités ou récupère celles qui étaient déjà en BD pour une Version
	 *
	 * @param treatmentVersion une version de traitement
	 * @throws AppServiceException erreur lors de la creatio des entities
	 */
	private void createEntities(TreatmentVersion treatmentVersion) throws AppServiceException {
		for (final TransientDtoReplacerTest getterOrCreator : transientDtoReplacers) {
			getterOrCreator.replaceDtoFor(treatmentVersion);
		}
	}

	private void createOkTreatments() throws Exception {
		val okTreatment = createTreatmentFromJson(JSON_BASIC_TREATMENT);
		val okTreatment2 = createTreatmentFromJson(JSON_BASIC_TREATMENT_WITH_UUID);
		val okTreatment3 = createTreatmentFromJson(JSON_RESEARCH_TREATMENT);
		val okTreatment4 = createTreatmentFromJson(OK_TREATMENT_4);
		val okTreatment5 = createTreatmentFromJson(OK_TREATMENT_5);

//		 Uuids des versions de traitement créés
		okTreatmentVersionUuid = okTreatment.getVersion().getUuid();
		okTreatmentVersion2Uuid = okTreatment2.getVersion().getUuid();
		okTreatmentVersion3Uuid = okTreatment3.getVersion().getUuid();
		okTreatmentVersion4Uuid = okTreatment4.getVersion().getUuid();
		okTreatmentVersion5Uuid = okTreatment5.getVersion().getUuid();

//		Uuids des traitements créés
		okTreatmentUuid = okTreatment.getUuid();
		okTreatment2Uuid = okTreatment2.getUuid();
		okTreatment3Uuid = okTreatment3.getUuid();
		okTreatment4Uuid = okTreatment4.getUuid();
	}

	private void createKoTreatments() throws Exception {
		createTreatmentFromJson(NOK_TREATMENT);
		Treatment withVersionNotValid = createTreatmentFromJson(NOK_TREATMENT_2);
		invalidVersionUuid = withVersionNotValid.getVersion().getUuid();
	}

	private AuthenticatedUser getKnownUser() {
		return getUserWithLogin("valid@mail.com");
	}

	private AuthenticatedUser getKnownUser2() {
		return getUserWithLogin("otherone@mail.com");
	}

	private AuthenticatedUser getKnownUser3() {
		return getUserWithLogin("searcher@mail.com");
	}

	private AuthenticatedUser getRandomUser() {
		return getUserWithLogin(RandomStringUtils.random(6));
	}

	private User authenticatedUserToUser(AuthenticatedUser originalUser, UUID userUuid) {
		User user = new User();
		user.setFirstname(originalUser.getFirstname());
		user.setLastname(originalUser.getLastname());
		user.setLogin(originalUser.getLogin());
		user.setUuid(userUuid);
		user.setType(org.rudi.facet.acl.bean.UserType.PERSON);
		return user;
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

	private Long countMyConsents() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		PagedConsentList page = myConsentsService.searchMyConsents(new ConsentSearchCriteria(), pageable);
		return page.getTotal();
	}

	private void checkConsent(Consent consent) {
		assertNotNull(consent.getUuid());
		assertNotNull(consent.getConsentDate());
		assertFalse(consent.getConsentDate().isAfter(OffsetDateTime.now()));
		assertNotNull(consent.getExpirationDate());
		assertNotNull(consent.getOwnerUuid());
		assertEquals(OwnerType.USER, consent.getOwnerType());
		assertNotNull(consent.getTreatment());
		assertNotNull(consent.getTreatmentVersion());
		assertEquals(TreatmentStatus.VALIDATED, consent.getTreatment().getStatus());
		assertEquals(TreatmentStatus.VALIDATED, consent.getTreatmentVersion().getStatus());
	}

	private User setConnectedUser(AuthenticatedUser authenticatedUser, UUID userUuid, List<UUID> organizationUuids)
			throws AppServiceException,
			DocumentStorageException {

		if(authenticatedUser == null){
			mockUnauthenticatedUser();
			return null;
		}

		User user = authenticatedUserToUser(authenticatedUser, userUuid);

		when(aclHelper.getAuthenticatedUserUuid()).thenReturn(userUuid);
		when(aclHelper.getAuthenticatedUser()).thenReturn(user);
		when(aclHelper.getUserByLogin(authenticatedUser.getLogin())).thenReturn(user);
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(authenticatedUser);

//		DocumentContent documentContent = new DocumentContent("", new File(""));
		when(organizationHelper.getMyOrganizationsUuids(ownerUuid1)).thenReturn(organizationUuids);
//		when(pdfConvertor.convertDocx2PDF(any())).thenReturn(documentContent);
//		when(pdfConvertor.convertPDF2PDFA(any())).thenReturn(documentContent);
//		when(pdfConvertor.validatePDFA(any())).thenReturn(new ValidationResult());
//		when(pdfSigner.sign(any(), any())).thenReturn(documentContent);
		doNothing().when(documentStorageService).storeDocument(any(), any(), any());
		return user;
	}

	private void mockUnauthenticatedUser() throws AppServiceUnauthorizedException {
		when(utilContextHelper.getAuthenticatedUser()).thenReturn(null);
		when(aclHelper.getAuthenticatedUser()).thenThrow(AppServiceUnauthorizedException.class);
		when(aclHelper.getAuthenticatedUserUuid()).thenThrow(AppServiceUnauthorizedException.class);
	}


	private List<Consent> createConsents(List<UUID> treatmentVersionUuids) throws AppServiceException {
		List<Consent> consents = new ArrayList<>();
		for (UUID treatmentVersionUuid : treatmentVersionUuids) {
			consents.add(consentsService.createConsent(treatmentVersionUuid));
		}
		return consents;
	}

	// ================== Tests POST consent ===========================

	/**
	 * Consentir avec un utilisateur inconnu => Répondre 401
	 */
	@Test
	void consent_with_unknown_user() throws AppServiceException {
		mockUnauthenticatedUser();
		assertThrows(AppServiceUnauthorizedException.class, () -> consentsService.createConsent(UUID.randomUUID()));
	}

	/**
	 * Consentir à une version de traitement inexistante => 500, les données sont dans un état invalide et interdit
	 */
	@Test
	void consent_to_non_existing_version() throws Exception {
		Long beforeTotal = countMyConsents();
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		assertThrows(AppServiceException.class, () -> consentsService.createConsent(UUID.randomUUID()));
		Long afterTotal = countMyConsents();
		assertEquals(beforeTotal, afterTotal);
	}

	/**
	 * Consentir à un traitement ayant une version mais n'étant pas valide => 4XX c'est interdit d'un point de vue métier
	 */
	@Test
	void consent_to_not_valid_version() throws Exception {
		Long beforeTotal = countMyConsents();
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		assertThrows(BusinessException.class, () -> consentsService.createConsent(invalidVersionUuid));
		Long afterTotal = countMyConsents();
		assertEquals(beforeTotal, afterTotal);
	}

	/**
	 * Consentir à un traitement OK : - Ayant une version valide - En étant un user connu, et en émettant un consentement au niveau ownertype = user =>
	 * 200 OK
	 */
	@Test
	void consent_to_valid_treatment() throws Exception {
		Long beforeTotal = countMyConsents();
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		Consent consent = consentsService.createConsent(okTreatmentVersion3Uuid);
		checkConsent(consent);
		Long afterTotal = countMyConsents();
		assertEquals(beforeTotal + 1, afterTotal);
	}

	/**
	 * Consentir à plusieurs traitement OK : - Ayant une version valide - En étant un user connu, et en émettant un consentement au niveau ownertype =
	 * user => 200 OK
	 */
	@Test
	void consent_to_valid_treatments() throws Exception {
		Long beforeTotal = countMyConsents();
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		List<Consent> consents = new ArrayList<>();
		consents.add(consentsService.createConsent(okTreatmentVersion3Uuid));
		consents.add(consentsService.createConsent(okTreatmentVersion4Uuid));
		consents.forEach(this::checkConsent);
		Long afterTotal = countMyConsents();
		assertEquals(beforeTotal + 2, afterTotal);
	}

	// ================== Tests GET my-consent ===========================

	/**
	 * Rechercher mes consentements sans être un utilisateur connu => 401
	 */
	@Test
	void searchMyConsents_with_unknown_user() throws AppServiceException {
		mockUnauthenticatedUser();
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		assertThrows(AppServiceUnauthorizedException.class,
				() -> myConsentsService.searchMyConsents(new ConsentSearchCriteria(), pageable));
	}

	/**
	 * Rechercher mes consentements sans jamais avoir consenti => Répondre une liste de consentements vides
	 */
	@Test
	void searchMyConsents_with_user_never_consented() throws Exception {
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		createConsents(List.of(okTreatmentVersion3Uuid));
		setConnectedUser(getRandomUser(), UUID.randomUUID(), new ArrayList<>());
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		PagedConsentList page = myConsentsService.searchMyConsents(new ConsentSearchCriteria(), pageable);
		assertEquals(0, page.getElements().size());
		assertEquals(0, page.getTotal());
	}

	/**
	 * Rechercher mes consentements ayant été émis dans une période précisée invalide => Si rien ne correspond à une recherche, on renvoie une liste vide
	 */
	@Test
	void searchMyConsents_in_invalid_period() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		OffsetDateTime minimum = OffsetDateTime.parse("2020-01-01T15:20:30+08:00");
		OffsetDateTime maximum = OffsetDateTime.parse("2022-01-01T15:20:30+08:00");
		criteria.setAcceptDateMin(maximum);
		criteria.setAcceptDateMax(minimum);
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		val result = myConsentsService.searchMyConsents(criteria, pageable);
		assertThat(result.getElements()).as("Page de consentements retournée").isEmpty();
	}

	/**
	 * Rechercher mes consentements expirants dans une période précisée invalide => Si rien ne correspond à une recherche, on renvoie une liste vide
	 */
	@Test
	void searchMyConsents_expiring_in_invalid_period() throws Exception {
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		OffsetDateTime minimum = OffsetDateTime.parse("2020-01-01T15:20:30+08:00");
		OffsetDateTime maximum = OffsetDateTime.parse("2022-01-01T15:20:30+08:00");
		criteria.setExpirationDateMin(maximum);
		criteria.setExpirationDateMax(minimum);
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		val result = myConsentsService.searchMyConsents(criteria, pageable);
		assertThat(result.getElements()).as("Page de consentements retournée").isEmpty();
	}

	/**
	 * Rechercher mes consentements ayant été émis dans une période donnée => Répondre les consentements donnés dans la période => Ne pas répondre les
	 * consentements hors de la période
	 *
	 */
	@ParameterizedTest
	@MethodSource("getValidPeriods")
	void searchMyConsents_in_period(OffsetDateTime minDate, OffsetDateTime maxDate) throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		List<Consent> consentsBefore = createConsents(List.of(okTreatmentVersion3Uuid));
		OffsetDateTime start = OffsetDateTime.now();
		List<Consent> consentsAfterStart = createConsents(List.of(okTreatmentVersion4Uuid));
		OffsetDateTime end = OffsetDateTime.now();
		List<Consent> consentsAfterEnd = createConsents(List.of(okTreatmentVersion5Uuid));

		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		if (minDate != null) {
			criteria.setAcceptDateMin(start);
		}
		if (maxDate != null) {
			criteria.setAcceptDateMax(end);
		}
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		PagedConsentList searched = myConsentsService.searchMyConsents(criteria, pageable);

		if (minDate == null && maxDate == null) {
			assertEquals(searched.getElements().size(),
					consentsBefore.size() + consentsAfterStart.size() + consentsAfterEnd.size());
		}

		assertFalse(CollectionUtils.isEmpty(searched.getElements()));
		searched.getElements().forEach(consent -> {
			if (minDate != null) { // Les consentements renvoyés sont après la date de debut de recherche (start)
				assertTrue(consent.getConsentDate().isAfter(start) || consent.getConsentDate().isEqual(start));
			}
			if (maxDate != null) { // Les consentements renvoyés sont avant la date de max de recherche (end)
				assertTrue(consent.getConsentDate().isBefore(end) || consent.getConsentDate().isEqual(end));
			}
		});
	}

	/**
	 * Rechercher mes consentements expirants dans une période donnée => Répondre les consentements expirants dans la période => Ne pas répondre les
	 * consentements expirants hors de la période
	 */
	@ParameterizedTest
	@MethodSource("getValidPeriods")
	void searchMyConsents_expiring_in_period(OffsetDateTime minDate, OffsetDateTime maxDate) throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		List<Consent> consents = createConsents(
				List.of(okTreatmentVersion3Uuid, okTreatmentVersion4Uuid, okTreatmentVersion5Uuid));

		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		criteria.setExpirationDateMin(minDate);
		criteria.setExpirationDateMax(maxDate);

		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		PagedConsentList searched = myConsentsService.searchMyConsents(criteria, pageable);

		if (minDate == null && maxDate == null) {
			assertEquals(searched.getElements().size(), consents.size());
		}

		searched.getElements().forEach(consent -> {
			if (minDate != null) {
				assertTrue(
						consent.getExpirationDate().isAfter(minDate) || consent.getExpirationDate().isEqual(minDate));
			}
			if (maxDate != null) {
				assertTrue(
						consent.getExpirationDate().isBefore(maxDate) || consent.getExpirationDate().isEqual(maxDate));
			}
		});
	}

	/**
	 * Récupération de périodes valides - null, null - minDate, null - null, maxDate - minDate, maxDate
	 *
	 * @return une date minimale et une date maximale
	 */
	private static Stream<Arguments> getValidPeriods() {
		return Stream.of(Arguments.of(null, null), Arguments.of(expirationDate2, null),
				Arguments.of(null, expirationDate2), Arguments.of(expirationDate2, expirationDate2));
	}

	/**
	 * Recherche de mes consentements sans remonter le consentement des autres => création de 2 consentements 1 pour un autre et 1 pour moi => recherche
	 * sans critère, on remonte que les consentements que j'ai émis
	 */
	@Test
	void searchMyConsents_not_otherConsents() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		createConsents(List.of(okTreatmentVersion3Uuid));

		setConnectedUser(getKnownUser2(), UUID.randomUUID(), new ArrayList<>());
		createConsents(List.of(okTreatmentVersion4Uuid));

		val pageable = utilPageable.getPageable(0, 10, "consentDate");

		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>()); // Se reconnecter avec notre bon user
		PagedConsentList searched = myConsentsService.searchMyConsents(new ConsentSearchCriteria(), pageable);
		assertTrue(searched.getElements().stream()
				.anyMatch(consent -> okTreatmentVersion3Uuid.equals(consent.getTreatmentVersion().getUuid())));
		assertTrue(searched.getElements().stream()
				.noneMatch(consent -> okTreatmentVersion4Uuid.equals(consent.getTreatmentVersion().getUuid())));
	}

	/**
	 * Recherche de mes consentements pour les traitements d'UUID donnés => Répondre mes consentements concernant les traitements donnés => Ne pas
	 * répondre mes consentements ne concernant pas les traitements donnés
	 */
	@Test
	void searchMyConsents_for_treatments() throws Exception {

		// Création de 3 consentements
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		createConsents(
				List.of(okTreatmentVersion3Uuid, okTreatmentVersion4Uuid, okTreatmentVersion5Uuid));

		// Je cherche mes consentements sur le traitement 3
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		criteria.setTreatmentUuids(List.of(okTreatment3Uuid));
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		PagedConsentList searched = myConsentsService.searchMyConsents(criteria, pageable);
		assertTrue(searched.getElements().stream()
				.anyMatch(consent -> okTreatmentVersion3Uuid.equals(consent.getTreatmentVersion().getUuid())));
		assertTrue(searched.getElements().stream()
				.noneMatch(consent -> okTreatmentVersion4Uuid.equals(consent.getTreatmentVersion().getUuid())));
		assertTrue(searched.getElements().stream()
				.noneMatch(consent -> okTreatmentVersion5Uuid.equals(consent.getTreatmentVersion().getUuid())));

		// Je cherche mes consentements sur le traitement 4
		criteria = new ConsentSearchCriteria();
		criteria.setTreatmentUuids(List.of(okTreatment4Uuid));
		searched = myConsentsService.searchMyConsents(criteria, pageable);
		assertTrue(searched.getElements().stream()
				.noneMatch(consent -> okTreatmentVersion3Uuid.equals(consent.getTreatmentVersion().getUuid())));
		assertTrue(searched.getElements().stream()
				.anyMatch(consent -> okTreatmentVersion4Uuid.equals(consent.getTreatmentVersion().getUuid())));
		assertTrue(searched.getElements().stream()
				.noneMatch(consent -> okTreatmentVersion5Uuid.equals(consent.getTreatmentVersion().getUuid())));

		// Je cherche mes consentements sur les traitements 3 et 4
		criteria = new ConsentSearchCriteria();
		criteria.setTreatmentUuids(List.of(okTreatment3Uuid, okTreatment4Uuid));
		searched = myConsentsService.searchMyConsents(criteria, pageable);
		assertTrue(searched.getElements().stream()
				.anyMatch(consent -> okTreatmentVersion3Uuid.equals(consent.getTreatmentVersion().getUuid())));
		assertTrue(searched.getElements().stream()
				.anyMatch(consent -> okTreatmentVersion4Uuid.equals(consent.getTreatmentVersion().getUuid())));
		assertTrue(searched.getElements().stream()
				.noneMatch(consent -> okTreatmentVersion5Uuid.equals(consent.getTreatmentVersion().getUuid())));
	}

	/**
	 * Recherche de mes consentements concernant un traitement inconnu => Aucun consentement renvoyé
	 */
	@Test
	void searchMyConsents_for_unknown_treatment() throws Exception {
		setConnectedUser(getKnownUser(), TREATMENT_OWNER_UUID, new ArrayList<>());
		createConsents(List.of(okTreatmentVersion3Uuid));

		// Recherche d'un traitement n'existant pas => RIEN
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		criteria.setTreatmentUuids(List.of(UUID.randomUUID()));
		val pageable = utilPageable.getPageable(0, 10, "consentDate");
		PagedConsentList searched = myConsentsService.searchMyConsents(criteria, pageable);
		assertTrue(CollectionUtils.isEmpty(searched.getElements()));

		// Recherche du traitement existant bien => OK
		criteria.setTreatmentUuids(List.of(okTreatment3Uuid));
		searched = myConsentsService.searchMyConsents(criteria, pageable);
		assertFalse(CollectionUtils.isEmpty(searched.getElements()));
		searched.getElements()
				.forEach(consent -> assertEquals(okTreatmentVersion3Uuid, consent.getTreatmentVersion().getUuid()));
	}

	// ================== Tests GET my-treatments-consents ===========================

	/**
	 * Recherche des consentements des autres concernant mes traitements en étant un utilisateur inconnu => Liste vide
	 */
	@Test
	void searchMyTreatmentsConsents_for_unknown_user() throws AppServiceException {
		mockUnauthenticatedUser();

		assertThrows(AppServiceUnauthorizedException.class, () -> consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria()));
	}

	@Test
	void revokeConsent() throws DocumentStorageException, AppServiceException {
		// Un user créé des consentements pour des traitements owné par 1, 3 et l'organisation 2
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());

		val consents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		if(CollectionUtils.isNotEmpty(consents.getElements())){
			consentsService.revokeConsent(consents.getElements().get(0).getUuid());
		}
	}

	@Test
	void revokeConsent_checkConsentValidities_unkonwnConsent() throws DocumentStorageException, AppServiceException {
		// Un user créé des consentements pour des traitements owné par 1, 3 et l'organisation 2
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		// vérifie le bon fonctionne de l'algo en cas de UUID ne renvoyant aucun consentement
		consentsService.checkConsentValidities(List.of(UUID.randomUUID()));
	}

	/**
	 * Recherche des consentements des autres concernant mes traitements en étant un utilisateur d'une organisation => Répondre les consentements
	 * concernant les traitements dont je suis le owner => Répondre les consentements concernant les traitements dont l'owner est l'organisation dont je
	 * fais partie
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_user_in_organisation() throws AppServiceException,
			DocumentStorageException {

		// Autre user consent, je own le 1 et mon organisation owne le 2
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		List<Consent> otherConsents = createConsents(List.of(okTreatmentVersion3Uuid, okTreatmentVersion4Uuid));

		// je suis user 2 et je dis que mon uuid est le uuid du owner du traitement 1
		// je dis que mon organisation est celle qui owne le traitement 2
		List<UUID> myOrganizations = List.of(ownerOrganisationUuid2);
		User user = setConnectedUser(getKnownUser2(), ownerUuid1, myOrganizations);
		assertNotNull(user);
		// cherche les consentements de mes traitements, et vérifie que j'ai bien ceux que je veux
		// vérifie également que je suis le owner de chaque traitement OU que mon organisation owne chaque traitement
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		assertTrue(myTreatmentsConsents.getElements().containsAll(otherConsents));
		myTreatmentsConsents.getElements()
				.forEach(consent -> assertTrue(user.getUuid().equals(consent.getTreatment().getOwnerUuid())
						|| myOrganizations.contains(consent.getTreatment().getOwnerUuid())));
	}

	/**
	 * Recherche des consentements des autres concernant mes traitements en étant pas un utilisateur d'une organisation => Répondre les consentements
	 * concernant les traitements dont je suis le owner
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_user_not_in_organisation() throws AppServiceException,
			DocumentStorageException {

		// Je own le traitement 1 uniquement
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		List<Consent> otherConsents = createConsents(List.of(okTreatmentVersionUuid));

		// Quand je récupère les organisations du chercheur, je n'en récupère pas
		List<UUID> myOrganisations = new ArrayList<>();
		User user = setConnectedUser(getKnownUser2(), UUID.randomUUID(), myOrganisations);
		assertNotNull(user);
		// Je vérifie que j'ai bien les consentements que je cherche et que pour chacun d'entre eux
		// je ne suis que le owner du traitement
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		assertTrue(myTreatmentsConsents.getElements().containsAll(otherConsents));
		myTreatmentsConsents.getElements().forEach(consent -> {
			assertEquals(user.getUuid(), consent.getTreatment().getOwnerUuid());
			assertFalse(myOrganisations.contains(consent.getTreatment().getOwnerUuid()));
		});
	}

	/**
	 * Recherche des consentements des autres concernant mes traitements en étant un utilisateur d'une organisation => Ne remonte pas de consentement
	 * concernant un traitement d'une autre organisation
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_user_in_organisation_no_consent_from_other_organisation()
			throws AppServiceException, DocumentStorageException {

		// Je own le traitement 1 mais pas le traitement 2, il est own par une organisation que j'ai donc c'est bon
		// mais le traitement 4 est own par une autre organisation
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		createConsents(List.of(okTreatmentVersionUuid, okTreatmentVersion2Uuid));
		List<Consent> otherConsentsNotRetrieved = createConsents(List.of(okTreatmentVersion4Uuid));

		// Quand je récupère les organisations du chercheur, je ne récupère que celle qui owne le traitement 2
		List<UUID> myOrganisations = List.of(ownerOrganisationUuid2);
		setConnectedUser(getKnownUser2(), UUID.randomUUID(), myOrganisations);

		// Je vérifie que pour tous les consentements récupérés aucun ne me concerne pas
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		assertFalse(CollectionUtils.isEmpty(myTreatmentsConsents.getElements()));
		myTreatmentsConsents.getElements().forEach(consent -> assertFalse(otherConsentsNotRetrieved.contains(consent)));
	}

	/**
	 * Recherche des consentements des autres concernant mes traitements => Ne remonte pas de consentement concernant un traitement ne m'appartenant pas
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_no_consent_from_other_owner() throws AppServiceException,
			DocumentStorageException {

		// Je own le traitement 1 mais pas le traitement 3
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		List<Consent> otherConsents = createConsents(List.of(okTreatmentVersionUuid));
		List<Consent> otherConsentsNotRetrieved = createConsents(List.of(okTreatmentVersion3Uuid));

		// Quand je récupère les organisations du chercheur, je ne récupère rien
		List<UUID> myOrganisations = new ArrayList<>();
		setConnectedUser(getKnownUser2(), UUID.randomUUID(), myOrganisations);

		// Je vérifie que pour tous les consentements récupérés aucun ne me concerne pas
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		assertTrue(myTreatmentsConsents.getElements().containsAll(otherConsents));
		myTreatmentsConsents.getElements().forEach(consent -> assertFalse(otherConsentsNotRetrieved.contains(consent)));
	}

	/**
	 * Recherche des consentements des autres alors que je n'ai aucun traitement => ne répondre aucun consentement
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_user_without_treatments() throws AppServiceException,
			DocumentStorageException {

		// Un autre user crée un consentement owné par l'user ownerUuid1
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		createConsents(List.of(okTreatmentVersionUuid));

		// Le user qui cherche a un UUID random = il ne peut pas avoir un traitement avec son UUID
		List<UUID> myOrganisations = new ArrayList<>();
		setConnectedUser(getKnownUser2(), UUID.randomUUID(), myOrganisations);

		// Je vérifie que je ne récupère aucun consentement
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		assertTrue(CollectionUtils.isEmpty(myTreatmentsConsents.getElements()));

		// Alors que si j'ai le bon UUID c'est bon
		User user = setConnectedUser(getKnownUser2(), ownerUuid1, myOrganisations);
		assertNotNull(user);
		myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		assertFalse(CollectionUtils.isEmpty(myTreatmentsConsents.getElements()));
		myTreatmentsConsents.getElements()
				.forEach(consent -> assertEquals(consent.getTreatment().getOwnerUuid(), user.getUuid()));
	}

	/**
	 * Recherche des consentements des autres sur mes traitements en ne souhaitant récupérer que ceux concernant un owner n'existant pas => Aucun
	 * consentement remonté
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_unknown_owner() throws AppServiceException,
			DocumentStorageException {

		// Un autre user crée un consentement owné par l'user ownerUuid1
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		List<Consent> otherConsents = createConsents(List.of(okTreatmentVersionUuid));

		// Je configure un chercheur qui pourrait remonter le consentement lié au traitement 1
		List<UUID> myOrganisations = new ArrayList<>();
		setConnectedUser(getKnownUser2(), ownerUuid1, myOrganisations);

		// Je vérifie bien ça
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		assertTrue(myTreatmentsConsents.getElements().containsAll(otherConsents));

		// Maintenant je demande le consentement d'un user spécifique qui n'existe pas (UUID qui ne rime à rien)
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		criteria.setOwnerUuids(List.of(UUID.randomUUID()));

		// Je vérifie que je ne récupère aucun consentement
		myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(criteria);
		assertTrue(CollectionUtils.isEmpty(myTreatmentsConsents.getElements()));
	}

	/**
	 * Recherche des consentements des autres sur mes traitements en ne souhaitant récupérer que ceux ownés par les owners fourni (UUIDs) => Remonte les
	 * consentements concernant mes traitements ownés par le owner uuid fourni => Ne remonte pas les consentements concernant mes traitements ownés par
	 * des owners non fournis
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_consent_owners() throws AppServiceException,
			DocumentStorageException {

		// Un user créé un consentement à un de mes traitements
		User consentCreator1 = setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		assertNotNull(consentCreator1);
		List<Consent> consentsUser1 = createConsents(List.of(okTreatmentVersionUuid));

		// Un autre user créé un consentement à un de mes traitements
		User consentCreator2 = setConnectedUser(getKnownUser2(), UUID.randomUUID(), new ArrayList<>());
		assertNotNull(consentCreator2);
		List<Consent> consentsUser2 = createConsents(List.of(okTreatmentVersionUuid));

		// Je configure un chercheur qui pourrait remonter le consentement lié au traitement 1
		List<UUID> myOrganisations = new ArrayList<>();
		setConnectedUser(getKnownUser3(), ownerUuid1, myOrganisations);

		// Recherche de consentements sur mes traitements où le consenteur est le 1
		// on récupère que les consentements de 1
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		criteria.setOwnerUuids(List.of(consentCreator1.getUuid()));
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(criteria);
		assertTrue(myTreatmentsConsents.getElements().containsAll(consentsUser1));
		myTreatmentsConsents.getElements().forEach(consent -> {
			assertEquals(consent.getOwnerUuid(), consentCreator1.getUuid());
			assertNotEquals(consent.getOwnerUuid(), consentCreator2.getUuid());
		});

		// Recherche de consentements sur mes traitements où le consenteur est le 2
		// on récupère que les consentements de 2
		criteria = new ConsentSearchCriteria();
		criteria.setOwnerUuids(List.of(consentCreator2.getUuid()));
		myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(criteria);
		assertTrue(myTreatmentsConsents.getElements().containsAll(consentsUser2));
		myTreatmentsConsents.getElements().forEach(consent -> {
			assertEquals(consent.getOwnerUuid(), consentCreator2.getUuid());
			assertNotEquals(consent.getOwnerUuid(), consentCreator1.getUuid());
		});
	}

	/**
	 * Recherche des consentements des autres sur mes traitements dont l'UUID correspond à ceux founis => Remonte les consentements concernant mes
	 * traitements dont l'uuid est dans ceux fournis => Ne remonte pas les consentements concernant mes traitements dont l'uuid n'est pas dans ceux
	 * fournis
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_treatments() throws AppServiceException,
			DocumentStorageException {

		// Un user créé des consentements pour des traitements owné par 1, 3 et l'organisation 2
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		createConsents(
				List.of(okTreatmentVersionUuid, okTreatmentVersion2Uuid, okTreatmentVersion3Uuid));

		// Je configure un chercheur qui owne le traitement 1 et dont l'organisation owne le traitement 2
		// et ne peux jamais voir le 3
		List<UUID> myOrganisations = List.of(ownerOrganisationUuid2);
		setConnectedUser(getKnownUser2(), ownerUuid1, myOrganisations);

		// Recherche de consentements sur mes traitements en ne voulant que ceux du traitement 1
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		criteria.setTreatmentUuids(List.of(okTreatmentUuid));
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(criteria);
		assertTrue(myTreatmentsConsents.getElements().stream()
				.anyMatch(consent -> consent.getTreatment().getUuid().equals(okTreatmentUuid)));
		assertTrue(myTreatmentsConsents.getElements().stream()
				.noneMatch(consent -> consent.getTreatment().getUuid().equals(okTreatment2Uuid)));
		assertTrue(myTreatmentsConsents.getElements().stream()
				.noneMatch(consent -> consent.getTreatment().getUuid().equals(okTreatment3Uuid)));

		// Recherche de consentements sur mes traitements en ne voulant que ceux du traitement 2
		criteria = new ConsentSearchCriteria();
		criteria.setTreatmentUuids(List.of(okTreatment2Uuid));
		myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(criteria);
		assertTrue(myTreatmentsConsents.getElements().stream()
				.noneMatch(consent -> consent.getTreatment().getUuid().equals(okTreatmentUuid)));
		assertTrue(myTreatmentsConsents.getElements().stream()
				.anyMatch(consent -> consent.getTreatment().getUuid().equals(okTreatment2Uuid)));
		assertTrue(myTreatmentsConsents.getElements().stream()
				.noneMatch(consent -> consent.getTreatment().getUuid().equals(okTreatment3Uuid)));
	}

	/**
	 * Recherche des consentements des autres sur un traitement qui ne m'appartient pas - treatment owner != moi - treatment owner organisation != une de
	 * mes organisations => aucun consentement renvoyé
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_for_another_treatment() throws AppServiceException,
			DocumentStorageException {

		// Un user créé des consentements pour des traitements owné par 1, 3 et l'organisation 2
		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		createConsents(
				List.of(okTreatmentVersionUuid, okTreatmentVersion2Uuid, okTreatmentVersion3Uuid));

		// Je configure un chercheur qui owne le traitement 1 et dont l'organisation owne le traitement 2
		// et le traitement 3 ne le regarde pas du tout
		List<UUID> myOrganisations = List.of(ownerOrganisationUuid2);
		setConnectedUser(getKnownUser2(), ownerUuid1, myOrganisations);

		// Recherche de consentements sur le traitement qui ne me concerne pas du tout => je trouve rien
		ConsentSearchCriteria criteria = new ConsentSearchCriteria();
		criteria.setTreatmentUuids(List.of(okTreatment3Uuid));
		PagedConsentList myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(criteria);
		assertTrue(myTreatmentsConsents.getElements().isEmpty());

		// Je change de chercheur pour pouvoir récupérer le traitement 3 et ça fonctionne pour lui
		setConnectedUser(getKnownUser3(), ownerUuid3, myOrganisations);
		criteria.setTreatmentUuids(List.of(okTreatment3Uuid));
		myTreatmentsConsents = consentsService.searchMyTreatmentsConsents(criteria);
		assertFalse(myTreatmentsConsents.getElements().isEmpty());
		myTreatmentsConsents.getElements()
				.forEach(consent -> assertEquals(consent.getTreatment().getUuid(), okTreatment3Uuid));
	}

	/**
	 * Création de consentements INVALIDES, lié à des traitements invalides : - traitement sans version - traitement avec une version non valide -
	 * traitement inexistant
	 * <p>
	 * Recherche des consentements des autres sur mes traitements qui sont invalides => Les consentements invalides ne sont pas remontés
	 */
	@Test
	@Disabled
	void searchMyTreatmentsConsents_consents_are_valid() throws AppServiceException,
			DocumentStorageException {

		setConnectedUser(getKnownUser(), UUID.randomUUID(), new ArrayList<>());
		List<Consent> myConsents = createConsents(List.of(okTreatmentVersionUuid));

		// TODO avec les DAOS créer directement les ConsentEntity malformés et récupérer des DTO Consent
		List<Consent> myInvalidConsents = new ArrayList<>();

		PagedConsentList searched = consentsService.searchMyTreatmentsConsents(new ConsentSearchCriteria());
		searched.getElements().forEach(consent -> {
			assertFalse(myInvalidConsents.contains(consent));
			checkConsent(consent);
		});
		assertTrue(searched.getElements().containsAll(myConsents));
	}

}
