package org.rudi.microservice.projekt.service.helper.linkeddataset;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetStatus;
import org.rudi.microservice.projekt.storage.dao.linkeddataset.LinkedDatasetCustomDao;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;


@Component
@RequiredArgsConstructor
@Slf4j
public class LinkedDatasetExpirationHelper {
	private final LinkedDatasetCustomDao linkedDatasetCustomDao;
	private final LinkedDatasetSubscriptionHelper linkedDatasetSubscriptionHelper;

	private final String RESTRICTED_LINKED_DATASET = "RESTRICTED";
	@Value("${rudi.projekt.tentative-max-suppression.linked-dataset-expired: 3}")
	private int NBRE_TENTATIVE_MAX_SUPPRESSION;

	public List<LinkedDatasetEntity> getValidatedLinkedDatasetExpired() {
		val criteria = new LinkedDatasetSearchCriteria()
				.datasetConfidentiality(RESTRICTED_LINKED_DATASET)
				.status(List.of(LinkedDatasetStatus.VALIDATED))
				.endDateIsOver(true);
		val restrictedValidatedLinkedDataset = linkedDatasetCustomDao.searchLinkedDatasets(criteria, Pageable.unpaged());
		return restrictedValidatedLinkedDataset.getContent();
	}

	public void cleanLinkedDatasetExpired(List<LinkedDatasetEntity> listToClean) {
		final AtomicInteger nbreTotalTentative = new AtomicInteger();
		final AtomicInteger nbreTotalTentativeEchouee = new AtomicInteger();
		final AtomicInteger nbreTotalTentativeSuccess = new AtomicInteger();
		listToClean.forEach(element -> {
			int nbreTentative = 0;
			boolean hasSucceded = false;
			while (!hasSucceded && nbreTentative < NBRE_TENTATIVE_MAX_SUPPRESSION) {
				try {
					linkedDatasetSubscriptionHelper.handleUnlinkLinkedDataset(element);
					hasSucceded = true;
					log.debug(String.format("Suppression de la souscription accordée par le linked dataset expirée (%s) effectuée avec succès", element.getUuid()));
					nbreTotalTentativeSuccess.addAndGet(1);
				} catch (Exception exception) {
					nbreTotalTentativeEchouee.addAndGet(1);
					log.warn(String.format("Tentative %d de suppresion de la souscription à liée au linked dataset %s échouée", nbreTentative + 1, element.getUuid()));
				} finally {
					nbreTentative += 1;
					nbreTotalTentative.addAndGet(1);
				}
			}
		});
		log.debug("-- Recapitulatif final --");
		log.debug(String.format("Nombre total d'éléments à traiter : %d", listToClean.size()));
		log.debug(String.format("Nombre total de tentative ayant réussie : %d", nbreTotalTentativeSuccess.get()));
		log.debug(String.format("Nombre total de tentative ayant échouée : %d", nbreTotalTentativeEchouee.get()));
		log.debug(String.format("Nombre total de tentative effectuée : %d", nbreTotalTentative.get()));
		log.debug("-- Fin recapitulatif final --");
		logLinkedDatasetWithoutEndDate();
	}

	private void logLinkedDatasetWithoutEndDate() {
		val withoutEndDate = getValidatedLinkedDatasetWithoutEndDate();
		log.debug("Liste des linked dataset sans date de fin");
		log.debug(String.format("Nombre total d'éléments à lister : %d", withoutEndDate.size()));
		withoutEndDate.forEach(element -> log.debug(String.format("UUID du linked dataset : %s", element.getUuid())));
	}

	public List<LinkedDatasetEntity> getValidatedLinkedDatasetWithoutEndDate() {
		val criteria = new LinkedDatasetSearchCriteria()
				.datasetConfidentiality(RESTRICTED_LINKED_DATASET)
				.status(List.of(LinkedDatasetStatus.VALIDATED))
				.endDateIsNull(true);
		val linkedDatasetWithoutEndDate = linkedDatasetCustomDao.searchLinkedDatasets(criteria, Pageable.unpaged());
		return linkedDatasetWithoutEndDate.getContent();
	}
}