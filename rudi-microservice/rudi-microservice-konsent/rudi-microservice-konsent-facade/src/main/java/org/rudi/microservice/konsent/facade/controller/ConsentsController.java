package org.rudi.microservice.konsent.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSENT_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.PROJECT_MANAGER;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.konsent.core.bean.Consent;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.rudi.microservice.konsent.facade.controller.api.ConsentsApi;
import org.rudi.microservice.konsent.service.consent.ConsentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RestController
@RequiredArgsConstructor
public class ConsentsController implements ConsentsApi {
	private final UtilPageable utilPageable;
	private final ConsentsService consentsService;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSENT_ADMINISTRATOR + ", " + USER + ")")
	public ResponseEntity<Consent> createConsent(UUID treatmentVersionUuid) throws Exception {
		return ResponseEntity.ok(consentsService.createConsent(treatmentVersionUuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_KONSENT_ADMINISTRATOR + ", " + PROJECT_MANAGER + ")")
	public ResponseEntity<PagedConsentList> searchConsents(
			OffsetDateTime acceptDateMin, OffsetDateTime acceptDateMax, OffsetDateTime expirationDateMin,
			OffsetDateTime expirationDateMax, List<UUID> owners, List<UUID> treatments,
			Integer offset, Integer limit, String order) throws Exception {
		ConsentSearchCriteria searchCriteria = new ConsentSearchCriteria().ownerUuids(owners).treatmentUuids(treatments)
				.acceptDateMin(acceptDateMin).acceptDateMax(acceptDateMax).expirationDateMin(expirationDateMin)
				.expirationDateMax(expirationDateMax);
		val pageable = utilPageable.getPageable(offset, limit, order);
		return ResponseEntity.ok(consentsService.searchConsents(searchCriteria, pageable));
	}
}
