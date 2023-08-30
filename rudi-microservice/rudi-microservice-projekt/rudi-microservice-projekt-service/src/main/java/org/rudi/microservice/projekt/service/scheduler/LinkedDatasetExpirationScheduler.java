package org.rudi.microservice.projekt.service.scheduler;

import org.rudi.microservice.projekt.service.helper.linkeddataset.LinkedDatasetExpirationHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Component
@Slf4j
@RequiredArgsConstructor
public class LinkedDatasetExpirationScheduler {

	public static final String DEFAULT_DELAY = "0 1 * * * ?";
	private final LinkedDatasetExpirationHelper linkedDatasetExpirationHelper;

	@Scheduled(cron = "${rudi.projekt.scheduler.linked-dataset-expiration-cleaner.delay: " + DEFAULT_DELAY + "}")
	public void scheduleLinkedDatasetExpirationCleaner () {
		log.debug("Lancement journalier du LinkedDatasetExpirationCleaner : calcul des linked datasets expirés");
		// Call method for compute obsolète linked datasets
		val expiredLinkedDatasets = linkedDatasetExpirationHelper.getValidatedLinkedDatasetExpired();
		log.debug("Nombre d'éléments expirés qui seront traités : " + expiredLinkedDatasets.size());
		linkedDatasetExpirationHelper.cleanLinkedDatasetExpired(expiredLinkedDatasets);
	}
}
