/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.service.scheduler;

import java.util.Arrays;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.IntegrationRequestSearchCriteria;
import org.rudi.microservice.kalim.core.bean.ProgressStatus;
import org.rudi.microservice.kalim.service.integration.IntegrationRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler pour la prise en compte des éléments traités et dont le rapport doit être envoyé
 * 
 * @author FNI18300
 *
 */
@Component
public class ReportScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportScheduler.class);

	@Autowired
	private IntegrationRequestService integrationRequestService;

	@Scheduled(fixedDelayString = "${rudi.kalim.scheduler.sendReport.delay}")
	public void handleIntegrationRequestReport() {
		LOGGER.info("Start ReportScheduler...");
		IntegrationRequestSearchCriteria searchCriteria = new IntegrationRequestSearchCriteria();
		searchCriteria.setProgressStatus(Arrays.asList(ProgressStatus.INTEGRATION_HANDLED));
		Page<IntegrationRequest> integrationRequests = integrationRequestService
				.searchIntegrationRequests(searchCriteria, Pageable.unpaged());

		if (CollectionUtils.isNotEmpty(integrationRequests.getContent())) {
			LOGGER.info("ReportScheduler nothing find {}", integrationRequests.getTotalElements());
			for (IntegrationRequest integrationRequest : integrationRequests) {
				try {
					integrationRequestService.handleIntegrationRequest(integrationRequest.getUuid());
				} catch (Exception e) {
					LOGGER.info("ReportScheduler skip {} - {}", integrationRequest.getUuid(), e.getMessage());
				}
			}
		} else {
			LOGGER.info("TreatmentScheduler nothing to do.");
		}

		LOGGER.info("ReportScheduler done.");
	}
}
