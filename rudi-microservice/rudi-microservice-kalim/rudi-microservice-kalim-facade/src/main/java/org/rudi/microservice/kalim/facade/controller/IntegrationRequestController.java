package org.rudi.microservice.kalim.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KALIM_ADMINISTRATOR;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.IntegrationRequestPageResult;
import org.rudi.microservice.kalim.core.bean.IntegrationRequestSearchCriteria;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.facade.controller.api.IntegrationRequestApi;
import org.rudi.microservice.kalim.service.integration.IntegrationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegrationRequestController implements IntegrationRequestApi {

	@Autowired
	private IntegrationRequestService integrationRequestService;

	@Autowired
	private UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KALIM_ADMINISTRATOR + ")")
	public ResponseEntity<IntegrationRequestPageResult> searchIntegrationRequests(IntegrationStatus integrationStatus,
			OffsetDateTime creationDateMin, OffsetDateTime creationDateMax, OffsetDateTime treatmentDateMin,
			OffsetDateTime treatmentDateMax, OffsetDateTime sendRequestDateMin, OffsetDateTime sendRequestDateMax,
			UUID globalId, Integer offset, Integer limit, String order) {

		IntegrationRequestSearchCriteria integrationRequestSearchCriteria = new IntegrationRequestSearchCriteria();
		integrationRequestSearchCriteria.setCreationDateMax(creationDateMax);
		integrationRequestSearchCriteria.setCreationDateMin(creationDateMin);
		integrationRequestSearchCriteria.setSendRequestDateMax(sendRequestDateMax);
		integrationRequestSearchCriteria.setSendRequestDateMin(sendRequestDateMin);
		integrationRequestSearchCriteria.setTreatmentDateMax(treatmentDateMax);
		integrationRequestSearchCriteria.setTreatmentDateMin(treatmentDateMin);
		integrationRequestSearchCriteria.setIntegrationStatus(integrationStatus);
		integrationRequestSearchCriteria.setGlobalId(globalId);

		Pageable pageable = utilPageable.getPageable(offset, limit, order);

		Page<IntegrationRequest> integrationRequests = integrationRequestService
				.searchIntegrationRequests(integrationRequestSearchCriteria, pageable);

		IntegrationRequestPageResult result = new IntegrationRequestPageResult();
		result.setTotal(integrationRequests.getTotalElements());
		result.setElements(integrationRequests.getContent());
		return ResponseEntity.ok(result);
	}

}
