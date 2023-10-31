package org.rudi.microservice.projekt.service.territory;


import lombok.RequiredArgsConstructor;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.TerritorialScale;
import org.rudi.microservice.projekt.core.bean.TerritorialScaleSearchCriteria;
import org.rudi.microservice.projekt.service.ProjectSpringBootTest;
import org.rudi.microservice.projekt.service.territory.TerritorialScaleService;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleDao;
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
class TerritorialScaleServiceUT {

	private static final String CODE_DE_L_ECHELLE_DU_TERRITOIRE_MEGALOPOLE = "megalopole";
	private static final String CODE_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE = "metropole";
	private static final String JSON_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE = "territorial-scales/metropole.json";

	private final TerritorialScaleService territorialScaleService;
	private final TerritorialScaleDao territorialScaleDao;

	private final JsonResourceReader jsonResourceReader;

	@AfterEach
	void tearDown() {
		territorialScaleDao.deleteAll();
	}

	private TerritorialScale createTerritorialScaleFromJson(String jsonPath) throws IOException, AppServiceException {
		final TerritorialScale territorialScale = jsonResourceReader.read(jsonPath, TerritorialScale.class);
		return territorialScaleService.createTerritorialScale(territorialScale);
	}

	@Test
	void createTerritorialScaleWithoutUuid() throws IOException, AppServiceException {
		final TerritorialScale territorialScaleToCreate = jsonResourceReader.read(JSON_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE, TerritorialScale.class);
		territorialScaleToCreate.setUuid(null);

		final TerritorialScale createdTerritorialScale = territorialScaleService.createTerritorialScale(territorialScaleToCreate);

		assertThat(createdTerritorialScale.getUuid())
				.as("Même si on n'indique pas d'UUID à la création d'une échelle de territoire, il est automatiquement généré")
				.isNotNull();
	}

	@Test
	void createTerritorialScaleWithUuid() throws IOException, AppServiceException {
		final TerritorialScale territorialScaleToCreate = jsonResourceReader.read(JSON_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE, TerritorialScale.class);
		final UUID forcedUuid = UUID.randomUUID();
		territorialScaleToCreate.setUuid(forcedUuid);

		final TerritorialScale createdTerritorialScale = territorialScaleService.createTerritorialScale(territorialScaleToCreate);

		assertThat(createdTerritorialScale.getUuid())
				.as("Même si on indique un UUID à la création d'une échelle de territoire, il n'est pas pris en compte mais regénéré")
				.isNotEqualTo(forcedUuid);
	}

	@Test
	void getTerritorialScale() throws IOException, AppServiceException {
		final TerritorialScale territorialScaleToCreate = jsonResourceReader.read(JSON_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE, TerritorialScale.class);
		final TerritorialScale createdTerritorialScale = territorialScaleService.createTerritorialScale(territorialScaleToCreate);

		final TerritorialScale gotTerritorialScale = territorialScaleService.getTerritorialScale(createdTerritorialScale.getUuid());

		assertThat(gotTerritorialScale)
				.as("On retrouve l'échelle de territoire créée")
				.isEqualToComparingFieldByField(createdTerritorialScale);
	}

	@Test
	void searchTerritorialScales() throws IOException, AppServiceException {

		createTerritorialScaleFromJson(JSON_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE);
		createTerritorialScaleFromJson("territorial-scales/megalopole.json");

		val pageable = PageRequest.of(0, 2);
		final TerritorialScaleSearchCriteria searchCriteria = new TerritorialScaleSearchCriteria();
		searchCriteria.limit(2).offset(0);
		final Page<TerritorialScale> territorialScales = territorialScaleService.searchTerritorialScales(searchCriteria, pageable);

		assertThat(territorialScales).as("On retrouve uniquement les échelles de territoire attendues")
				.extracting("code")
				.containsOnly(CODE_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE, CODE_DE_L_ECHELLE_DU_TERRITOIRE_MEGALOPOLE);
	}

	@Test
	void updateTerritorialScale() throws IOException, AppServiceException {
		final TerritorialScale territorialScale = createTerritorialScaleFromJson(JSON_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE);
		territorialScale.setCode("nouveau_code");
		territorialScale.setLabel("Nouvelle étiquette");
		territorialScale.setOpeningDate(LocalDateTime.now());
		territorialScale.setClosingDate(LocalDateTime.now());
		territorialScale.setOrder(territorialScale.getOrder() + 1);

		final TerritorialScale updatedTerritorialScale = territorialScaleService.updateTerritorialScale(territorialScale);

		assertThat(updatedTerritorialScale)
				.as("Tous les champs sont bien modifiés")
				.isEqualToComparingFieldByField(territorialScale);
	}

	@Test
	void deleteTerritorialScale() throws IOException, AppServiceException {
		final long totalElementsBeforeCreate = countTerritorialScales();

		final TerritorialScale createdTerritorialScale = createTerritorialScaleFromJson(JSON_DE_L_ECHELLE_DU_TERRITOIRE_METROPOLE);
		final long totalElementsAfterCreate = countTerritorialScales();
		assertThat(totalElementsAfterCreate).as("L'échelle de territoire est bien créée").isEqualTo(totalElementsBeforeCreate + 1);

		territorialScaleService.deleteTerritorialScale(createdTerritorialScale.getUuid());
		final long totalElementsAfterDelete = countTerritorialScales();
		assertThat(totalElementsAfterDelete).as("L'échelle de territoire est bien supprimée").isEqualTo(totalElementsBeforeCreate);
	}

	private long countTerritorialScales() {
		val pageable = PageRequest.of(0, 100);
		return territorialScaleService.searchTerritorialScales(new TerritorialScaleSearchCriteria(), pageable).getTotalElements();
	}

}
