package org.rudi.microservice.projekt.service;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Confidentiality;
import org.rudi.microservice.projekt.core.bean.ConfidentialitySearchCriteria;
import org.rudi.microservice.projekt.core.bean.ProjectType;
import org.rudi.microservice.projekt.service.confidentiality.ConfidentialityService;
import org.rudi.microservice.projekt.storage.dao.confidentiality.ConfidentialityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Class de test de la couche service
 */
@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ConfidentialityServiceUT {

	private static final ConfidentialityServiceUT.KnownConfidentiality TOP_SECRET = new ConfidentialityServiceUT.KnownConfidentiality("top-secret");
	private static final ConfidentialityServiceUT.KnownConfidentiality TOUT_PUBLIC = new ConfidentialityServiceUT.KnownConfidentiality("tout-public");
	private static final ConfidentialityServiceUT.KnownConfidentiality TEST_CREATION = new ConfidentialityServiceUT.KnownConfidentiality("test-creation");

	private final ConfidentialityService confidentialityService;
	private final ConfidentialityDao confidentialityDao;

	private final JsonResourceReader jsonResourceReader;

	@AfterEach
	void tearDown() {
		confidentialityDao.deleteAll();
	}

	private Confidentiality createConfidentialityFromJson(String jsonPath) throws IOException, AppServiceException {
		final Confidentiality confidentiality = jsonResourceReader.read(jsonPath, Confidentiality.class);
		return getOrCreate(confidentiality);
	}

	@Test
	void createConfidentialityWithoutUuid() throws IOException, AppServiceException {
		final Confidentiality confidentialityToCreate = jsonResourceReader.read(TOP_SECRET.getJsonPath(), Confidentiality.class);
		confidentialityToCreate.setUuid(null);

		final Confidentiality createdConfidentiality = confidentialityService.createConfidentiality(confidentialityToCreate);

		assertThat(createdConfidentiality.getUuid())
				.as("Même si on n'indique pas d'UUID à la création d'un niveau de confidentialité, il est automatiquement généré")
				.isNotNull();
	}

	@Test
	void createConfidentialityWithUuid() throws IOException, AppServiceException {
		final Confidentiality confidentialityToCreate = jsonResourceReader.read(TOP_SECRET.getJsonPath(), Confidentiality.class);
		final UUID forcedUuid = UUID.randomUUID();
		confidentialityToCreate.setUuid(forcedUuid);

		final Confidentiality createdConfidentiality = confidentialityService.createConfidentiality(confidentialityToCreate);

		assertThat(createdConfidentiality.getUuid())
				.as("Même si on indique un UUID à la création d'un niveau de confidentialité, il n'est pas pris en compte mais regénéré")
				.isNotEqualTo(forcedUuid);
	}

	@Test
	void getConfidentiality() throws IOException, AppServiceException {
		final Confidentiality confidentialityToCreate = jsonResourceReader.read(TOP_SECRET.getJsonPath(), Confidentiality.class);
		final Confidentiality createdConfidentiality = confidentialityService.createConfidentiality(confidentialityToCreate);

		final Confidentiality gotConfidentiality = confidentialityService.getConfidentiality(createdConfidentiality.getUuid());

		assertThat(gotConfidentiality)
				.as("On retrouve le niveau de confidentialité créé")
				.isEqualToComparingFieldByField(createdConfidentiality);
	}

	@Test
	void searchConfidentialities() throws IOException, AppServiceException {
		Confidentiality confidentialityTopSecret = confidentialityService.getConfidentialityByCode("top-secret");
		Confidentiality confidentialityPublic = confidentialityService.getConfidentialityByCode("tout-public");
		if(confidentialityTopSecret == null) {
			createConfidentialityFromJson(TOP_SECRET.getJsonPath());
		}
		if(confidentialityPublic == null) {
			createConfidentialityFromJson(TOUT_PUBLIC.getJsonPath());
		}

		val pageable = PageRequest.of(0, 2);
		final ConfidentialitySearchCriteria searchCriteria = new ConfidentialitySearchCriteria();
		searchCriteria.limit(2).offset(0);
		final Page<Confidentiality> confidentialities = confidentialityService.searchConfidentialities(searchCriteria, pageable);

		assertThat(confidentialities).as("On retrouve uniquement le niveau de confidentialité attendu")
				.extracting("code")
				.containsOnly(TOP_SECRET.code, TOUT_PUBLIC.code);
	}

	@Test
	void updateConfidentiality() throws IOException, AppServiceException {
		Confidentiality confidentiality = confidentialityService.getConfidentialityByCode("top-secret");
		if(confidentiality == null) {
			confidentiality = createConfidentialityFromJson(TOP_SECRET.getJsonPath());
		}
		confidentiality.setCode("nouveau_code");
		confidentiality.setLabel("Nouvelle étiquette");
		confidentiality.setOpeningDate(LocalDateTime.now());
		confidentiality.setClosingDate(LocalDateTime.now());
		confidentiality.setOrder(confidentiality.getOrder() + 1);

		final Confidentiality updatedConfidentiality = confidentialityService.updateConfidentiality(confidentiality);

		assertThat(updatedConfidentiality)
				.as("Tous les champs sont bien modifiés")
				.isEqualToComparingFieldByField(confidentiality);
	}

	@Test
	void deleteConfidentiality() throws IOException, AppServiceException {
		final long totalElementsBeforeCreate = countConfidentialities();

		final Confidentiality createdConfidentiality = createConfidentialityFromJson(TEST_CREATION.getJsonPath());
		final long totalElementsAfterCreate = countConfidentialities();
		assertThat(totalElementsAfterCreate).as("Le niveau de confidentialité est bien créée").isEqualTo(totalElementsBeforeCreate + 1);

		confidentialityService.deleteConfidentiality(createdConfidentiality.getUuid());
		final long totalElementsAfterDelete = countConfidentialities();
		assertThat(totalElementsAfterDelete).as("Le niveau de confidentialité est bien supprimée").isEqualTo(totalElementsBeforeCreate);
	}

	private long countConfidentialities() {
		val pageable = PageRequest.of(0, 100);
		return confidentialityService.searchConfidentialities(new ConfidentialitySearchCriteria(), pageable).getTotalElements();
	}

	@Data
	private static class KnownConfidentiality {
		private final String code;
		private UUID uuid;

		String getJsonPath() {
			return "confidentialities/" + code + ".json";
		}
	}

	private Confidentiality getOrCreate(Confidentiality confidentiality) throws AppServiceException {
		Confidentiality finalConfidentiality = null;
		//On tente de récuperer la confidentiality de base
		if(confidentiality != null) {
			finalConfidentiality = confidentialityService.getConfidentialityByCode(confidentiality.getCode());
		}
		//Si c'est la première tentative de création de la confidentiality ayant ce code, on la crée
		if(finalConfidentiality == null && confidentiality != null) {
			return confidentialityService.createConfidentiality(confidentiality);
		}

		return finalConfidentiality;
	}
}
