package org.rudi.microservice.projekt.facade.controller;

import java.util.UUID;


import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.projekt.core.bean.PagedReutilisationStatusList;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatusSearchCriteria;
import org.rudi.microservice.projekt.facade.controller.api.ReutilisationStatusApi;
import org.rudi.microservice.projekt.service.reutilisationstatus.ReutilisationStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;

@RestController
@RequiredArgsConstructor
public class ReutilisationStatusController implements ReutilisationStatusApi {
	private final ReutilisationStatusService reutilisationStatusService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<ReutilisationStatus> createReutilisationStatus(ReutilisationStatus reutilisationStatus) {
		return ResponseEntity.ok(reutilisationStatusService.createReutilisationStatus(reutilisationStatus));
	}

	@Override
	public ResponseEntity<ReutilisationStatus> getReutilisationStatus(UUID uuid) {
		return ResponseEntity.ok(reutilisationStatusService.getReutilisationStatus(uuid));
	}

	@Override
	public ResponseEntity<PagedReutilisationStatusList> searchReutilisationStatus(ReutilisationStatusSearchCriteria criteria) {
		val pageable = utilPageable.getPageable(criteria.getOffset(), criteria.getLimit(), criteria.getOrder());
		val pages =  reutilisationStatusService.searchReutilisationStatus(criteria, pageable);
		return ResponseEntity.ok(new PagedReutilisationStatusList()
				.total(pages.getTotalElements())
				.elements(pages.getContent()));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<ReutilisationStatus> updateReutilisationStatus(UUID uuid, ReutilisationStatus reutilisationStatus) {
		return ResponseEntity.ok(reutilisationStatusService.updateReutilisationStatus(uuid, reutilisationStatus));
	}
}
