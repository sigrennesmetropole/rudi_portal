package org.rudi.microservice.projekt.service.helper.linkeddataset;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.rudi.microservice.projekt.core.bean.DatasetConfidentiality;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetCustomDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkedDatasetExpirationHelper {
	private final LinkedDatasetCustomDao linkedDatasetCustomDao;
	private final LinkedDatasetSubscriptionHelper linkedDatasetSubscriptionHelper;

	public List<LinkedDatasetEntity> getValidatedLinkedDatasetExpired() {
		val criteria = new LinkedDatasetSearchCriteria()
				.datasetConfidentiality(DatasetConfidentiality.RESTRICTED.getValue())
				.status(List.of(LinkedDatasetStatus.VALIDATED)).endDateIsOver(true);
		val restrictedValidatedLinkedDataset = linkedDatasetCustomDao.searchLinkedDatasets(criteria,
				Pageable.unpaged());
		return restrictedValidatedLinkedDataset.getContent();
	}

	public void cleanLinkedDatasetExpired(List<LinkedDatasetEntity> listToClean) {
		final AtomicInteger nbreTotalTentative = new AtomicInteger();
		final AtomicInteger nbreTotalTentativeEchouee = new AtomicInteger();
		final AtomicInteger nbreTotalTentativeSuccess = new AtomicInteger();
		listToClean.forEach(element -> {
			try {
				linkedDatasetSubscriptionHelper.cleanLinkedDatasetExpired(element);
				log.debug(
						"Suppression de la souscription accordée par le linked dataset expirée {} effectuée avec succès",
						element.getUuid());
				nbreTotalTentativeSuccess.addAndGet(1);
			} catch (Exception exception) {
				nbreTotalTentativeEchouee.addAndGet(1);
				log.warn("Tentative de suppression de la souscription liée au linked dataset {} échouée",
						element.getUuid(), exception);
			} finally {
				nbreTotalTentative.addAndGet(1);
			}
		});
		log.debug("-- Recapitulatif final --");
		log.debug(String.format("Nombre total d'éléments à traiter : %d", listToClean.size()));
		log.debug(String.format("Nombre total de tentatives ayant réussi : %d", nbreTotalTentativeSuccess.get()));
		log.debug(String.format("Nombre total de tentatives ayant échoué : %d", nbreTotalTentativeEchouee.get()));
		log.debug(String.format("Nombre total de tentatives effectuées : %d", nbreTotalTentative.get()));
		log.debug("-- Fin recapitulatif final --");
		logLinkedDatasetWithoutEndDate();
	}

	private void logLinkedDatasetWithoutEndDate() {
		val withoutEndDate = getRestrictedValidatedLinkedDatasetWithoutEndDate();
		log.debug(String.format("Nombre total de linked dataset validés et restreints sans date de fin : %d",
				withoutEndDate.size()));
		withoutEndDate.forEach(element -> log
				.debug(String.format("Le linked dataset d'UUID : %s n'a pas de date de fin.", element.getUuid())));
	}

	public List<LinkedDatasetEntity> getRestrictedValidatedLinkedDatasetWithoutEndDate() {
		val criteria = new LinkedDatasetSearchCriteria()
				.datasetConfidentiality(DatasetConfidentiality.RESTRICTED.getValue())
				.status(List.of(LinkedDatasetStatus.VALIDATED)).endDateIsNull(true);
		val linkedDatasetWithoutEndDate = linkedDatasetCustomDao.searchLinkedDatasets(criteria, Pageable.unpaged());
		return linkedDatasetWithoutEndDate.getContent();
	}
}