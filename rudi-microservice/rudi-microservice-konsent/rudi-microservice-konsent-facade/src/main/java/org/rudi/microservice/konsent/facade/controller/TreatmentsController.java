package org.rudi.microservice.konsent.facade.controller;

import java.util.List;
import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentList;
import org.rudi.microservice.konsent.core.bean.PagedTreatmentVersionList;
import org.rudi.microservice.konsent.core.bean.Treatment;
import org.rudi.microservice.konsent.core.bean.TreatmentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.TreatmentStatus;
import org.rudi.microservice.konsent.core.bean.TreatmentVersionSearchCriteria;
import org.rudi.microservice.konsent.facade.controller.api.TreatmentsApi;
import org.rudi.microservice.konsent.service.treatment.TreatmentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSENT_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

@RestController
@RequiredArgsConstructor
public class TreatmentsController implements TreatmentsApi {

	private final TreatmentsService treatmentsService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<Treatment> createTreatment(Treatment treatment) throws Exception {
		return ResponseEntity.ok(treatmentsService.createTreatment(treatment));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<Void> deleteTreatment(UUID uuid) throws Exception {
		treatmentsService.deleteTreatment(uuid);
		return ResponseEntity.ok().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<Void> deleteTreatmentVersion(UUID treatmentUuid, UUID versionUuid) throws Exception {
		treatmentsService.deleteTreatmentVersion(treatmentUuid, versionUuid);
		return ResponseEntity.ok().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<Treatment> getTreatment(UUID uuid, Boolean validated) throws Exception {
		val result = treatmentsService.getTreatment(uuid, validated);
		return ResponseEntity.ok(result);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<PagedTreatmentVersionList> searchTreatmentVersions(UUID uuid, Integer offset, Integer limit, String order) throws Exception {
		TreatmentVersionSearchCriteria searchCriteria = new TreatmentVersionSearchCriteria()
				.treatmentUuid(uuid)
				.offset(offset)
				.limit(limit)
				.order(order);
		val pageable = utilPageable.getPageable(offset, limit, order);
		return ResponseEntity.ok(treatmentsService.searchTreatmentVersions(searchCriteria, pageable));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<Treatment> publishTreatment(UUID uuid) throws Exception {
		return ResponseEntity.ok(treatmentsService.publishTreatment(uuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<PagedTreatmentList> searchTreatments(List<UUID> purposes, List<TreatmentStatus> treatmentStatus, Integer offset, Integer limit, String order) throws Exception {
		TreatmentSearchCriteria searchCriteria = new TreatmentSearchCriteria()
				.purposes(purposes)
				.treatmentStatuses(treatmentStatus)
				.offset(offset)
				.limit(limit)
				.order(order);
		val pageable = utilPageable.getPageable(offset, limit, order);
		return ResponseEntity.ok(treatmentsService.searchTreatments(searchCriteria, pageable));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<Treatment> updateTreatment(Treatment treatment) throws Exception {
		return ResponseEntity.ok(treatmentsService.updateTreatment(treatment));
	}
}