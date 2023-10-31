package org.rudi.microservice.projekt.service.reutilisationstatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.rudi.common.core.json.JsonResourceReader;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatusSearchCriteria;
import org.rudi.microservice.projekt.service.ProjectSpringBootTest;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ProjectSpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ReutilisationStatusServiceUT {

	private static final ReutilisationStatusServiceUT.KnownReutilisationStatus PROJECT = new ReutilisationStatusServiceUT.KnownReutilisationStatus(
			"project");
	private static final ReutilisationStatusServiceUT.KnownReutilisationStatus REUSE = new ReutilisationStatusServiceUT.KnownReutilisationStatus(
			"reuse");

	private final ReutilisationStatusDao reutilisationStatusDao;
	private final ReutilisationStatusService reutilisationStatusService;

	private final JsonResourceReader jsonResourceReader;

	@AfterEach
	void tearDown() {
		reutilisationStatusDao.deleteAll();
	}

	private void areIdentical(ReutilisationStatus statusToCreate, ReutilisationStatus createdStatus,
			boolean ignoreUuid) {
		harmonizeDate(statusToCreate);
		harmonizeDate(createdStatus);
		String ignore = ignoreUuid ? "uuid" : "";
		assertThat(createdStatus).as("On retrouve tous les champs du statut créé").usingRecursiveComparison()
				.ignoringFields(ignore).isEqualTo(statusToCreate);
	}

	private void harmonizeDate(ReutilisationStatus status) {
		status.setClosingDate(status.getClosingDate().truncatedTo(ChronoUnit.MINUTES));
		status.setOpeningDate(status.getOpeningDate().truncatedTo(ChronoUnit.MINUTES));
	}

	@Test
	void createReutilisationStatusOK() throws IOException {
		final ReutilisationStatus statusToCreate = jsonResourceReader.read(PROJECT.getJsonPath(),
				ReutilisationStatus.class);
		val createdStatus = reutilisationStatusService.createReutilisationStatus(statusToCreate);
		areIdentical(statusToCreate, createdStatus, true);
	}

	@Test
	void create2ReutilisationStatusOK() throws IOException {
		final ReutilisationStatus statusToCreate = jsonResourceReader.read(PROJECT.getJsonPath(),
				ReutilisationStatus.class);
		val createdStatus = reutilisationStatusService.createReutilisationStatus(statusToCreate);
		areIdentical(statusToCreate, createdStatus, true);

		final ReutilisationStatus status2ToCreate = jsonResourceReader.read(REUSE.getJsonPath(),
				ReutilisationStatus.class);
		val created2Status = reutilisationStatusService.createReutilisationStatus(status2ToCreate);
		areIdentical(status2ToCreate, created2Status, true);
	}

	@Test
	void createReutilisationSpecifyingUuidKO() throws IOException {
		final ReutilisationStatus statusToCreate = jsonResourceReader.read(PROJECT.getJsonPath(),
				ReutilisationStatus.class);
		final UUID uuid = statusToCreate.getUuid();
		val createdStatus = reutilisationStatusService.createReutilisationStatus(statusToCreate);
		assertNotEquals(uuid, createdStatus.getUuid(),
				"L'UUID saisi en amont ne doit pas être celui renseigné en BDD. Création d'un nouvel UUID.");
	}

	@Test
	void updateReutilisationOK() throws IOException {
		final ReutilisationStatus statusToCreate = jsonResourceReader.read(PROJECT.getJsonPath(),
				ReutilisationStatus.class);
		val createdStatus = reutilisationStatusService.createReutilisationStatus(statusToCreate);

		val uuid = createdStatus.getUuid();

		final ReutilisationStatus statusToUpdate = jsonResourceReader.read(REUSE.getJsonPath(),
				ReutilisationStatus.class);
		statusToUpdate.setUuid(uuid);
		val updatedStatus = reutilisationStatusService.updateReutilisationStatus(createdStatus.getUuid(),
				statusToUpdate);

		assertEquals(uuid, updatedStatus.getUuid());
		areIdentical(statusToUpdate, updatedStatus, false);

		harmonizeDate(createdStatus);
		harmonizeDate(updatedStatus);

		String ignore = "uuid";
		assertThat(createdStatus).as("On ne doit pas retrouver les champs du statut créé").usingRecursiveComparison()
				.ignoringFields(ignore).isNotEqualTo(updatedStatus);
	}

	@Test
	void getReutilisationStatusWrongUUID() {
		assertThrows(EmptyResultDataAccessException.class,
				() -> reutilisationStatusService.getReutilisationStatus(UUID.randomUUID()));
	}

	@Test
	void getReutilisationStatusOK() throws IOException {
		final ReutilisationStatus statusToCreate = jsonResourceReader.read(PROJECT.getJsonPath(),
				ReutilisationStatus.class);
		val createdStatus = reutilisationStatusService.createReutilisationStatus(statusToCreate);
		val uuid = createdStatus.getUuid();

		val gotStatus = reutilisationStatusService.getReutilisationStatus(uuid);
		areIdentical(gotStatus, createdStatus, false);
	}

	@Test
	void searchStatusOK() throws IOException {
		List<ReutilisationStatus> status = new ArrayList<>();
		final ReutilisationStatus statusToCreate = jsonResourceReader.read(PROJECT.getJsonPath(),
				ReutilisationStatus.class);
		val createdStatus = reutilisationStatusService.createReutilisationStatus(statusToCreate);
		status.add(createdStatus);
		final ReutilisationStatus status2ToCreate = jsonResourceReader.read(REUSE.getJsonPath(),
				ReutilisationStatus.class);
		val created2Status = reutilisationStatusService.createReutilisationStatus(status2ToCreate);
		status.add(created2Status);
		val criteria = new ReutilisationStatusSearchCriteria();
		val pageable = Pageable.unpaged();
		val pages = reutilisationStatusService.searchReutilisationStatus(criteria, pageable);
		assertEquals(status.size(), pages.getContent().size());

		for (int i = 0; i < status.size(); i++) {
			harmonizeDate(status.get(i));
			harmonizeDate(pages.getContent().get(i));
		}

		assertEquals(status, pages.getContent(), "Les deux listes doivent être dientiques");

	}

	@Data
	private static class KnownReutilisationStatus {
		private final String code;
		private UUID uuid;
		private boolean datasetSetModificationAllowed;

		String getJsonPath() {
			return "reutilisationstatus/" + code + ".json";
		}
	}

}
