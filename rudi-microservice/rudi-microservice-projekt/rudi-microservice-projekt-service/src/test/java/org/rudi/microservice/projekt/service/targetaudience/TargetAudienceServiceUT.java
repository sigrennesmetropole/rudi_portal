package org.rudi.microservice.projekt.service.targetaudience;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.service.ProjectSpringBootTest;
import org.rudi.microservice.projekt.service.targetaudience.TargetAudienceService;
import org.rudi.microservice.projekt.storage.dao.targetaudience.TargetAudienceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import lombok.RequiredArgsConstructor;

/**
 * Classe de test de la couche service
 */
@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class TargetAudienceServiceUT {
	private static final String JSON_EVERYBODY = "targetaudience/everybody.json";

	private final TargetAudienceService targetAudienceService;
	private final TargetAudienceDao targetAudienceDao;
	private final JsonResourceReader jsonResourceReader;

	@AfterEach
	void tearDown() {
		targetAudienceDao.deleteAll();
	}

	private TargetAudience createTargetAudienceFromJson(String jsonPath) throws IOException, AppServiceException {
		final TargetAudience targetAudience = jsonResourceReader.read(jsonPath, TargetAudience.class);
		return targetAudienceService.createTargetAudience(targetAudience);
	}

	@Test
	void createTargetAudienceWithoutUuid() throws IOException, AppServiceException {
		final TargetAudience targetAudienceToCreate = jsonResourceReader.read(JSON_EVERYBODY, TargetAudience.class);
		targetAudienceToCreate.setUuid(null);

		final TargetAudience createdTargetAudience = targetAudienceService.createTargetAudience(targetAudienceToCreate);

		assertThat(createdTargetAudience.getUuid())
				.as("Même si on n'indique pas d'UUID à la création d'un public cible, il est automatiquement généré")
				.isNotNull();
	}

	@Test
	void createTargetAudienceWithUuid() throws IOException, AppServiceException {
		final TargetAudience targetAudienceToCreate = jsonResourceReader.read(JSON_EVERYBODY, TargetAudience.class);
		final UUID forcedUuid = UUID.randomUUID();
		targetAudienceToCreate.setUuid(forcedUuid);

		final TargetAudience createdTargetAudience = targetAudienceService.createTargetAudience(targetAudienceToCreate);

		assertThat(createdTargetAudience.getUuid()).as(
				"Même si on indique un UUID à la création d'un public cible, il n'est pas pris en compte mais regénéré")
				.isNotEqualTo(forcedUuid);
	}

	@Test
	void getTargetAudience() throws IOException, AppServiceException {
		final TargetAudience targetAudienceToCreate = jsonResourceReader.read(JSON_EVERYBODY, TargetAudience.class);
		final TargetAudience createdTargetAudience = targetAudienceService.createTargetAudience(targetAudienceToCreate);

		final TargetAudience gotTargetAudience = targetAudienceService
				.getTargetAudience(createdTargetAudience.getUuid());

		assertThat(gotTargetAudience).as("On retrouve le public cible créé")
				.isEqualToComparingFieldByField(createdTargetAudience);
	}

	@Test
	void updateTargetAudience() throws IOException, AppServiceException {
		final TargetAudience targetAudience = createTargetAudienceFromJson(JSON_EVERYBODY);
		targetAudience.setCode("nouveau_code");
		targetAudience.setLabel("Nouvelle étiquette");
		targetAudience.setOpeningDate(LocalDateTime.now());
		targetAudience.setClosingDate(LocalDateTime.now());
		targetAudience.setOrder(targetAudience.getOrder() + 1);

		final TargetAudience updatedTargetAudience = targetAudienceService.updateTargetAudience(targetAudience);

		assertThat(updatedTargetAudience).as("Tous les champs sont bien modifiés")
				.isEqualToComparingFieldByField(targetAudience);
	}

	@Test
	void deleteTargetAudience() throws IOException, AppServiceException {
		final TargetAudience createdTargetAudience = createTargetAudienceFromJson(JSON_EVERYBODY);
		targetAudienceService.deleteTargetAudience(createdTargetAudience.getUuid());
		assertThatThrownBy(() -> targetAudienceDao.findByUUID(createdTargetAudience.getUuid()))
				.as("Le public cible est bien supprimé").isInstanceOf(EmptyResultDataAccessException.class);
	}
}
