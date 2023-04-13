package org.rudi.microservice.konsent.facade.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Parameter;
import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.konsent.core.bean.ConsentSearchCriteria;
import org.rudi.microservice.konsent.core.bean.PagedConsentList;
import org.rudi.microservice.konsent.facade.controller.api.MyConsentsApi;
import org.rudi.microservice.konsent.service.consent.MyConsentsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

@RestController
@RequiredArgsConstructor
public class MyConsentsController implements MyConsentsApi {

	private final MyConsentsService myConsentsService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + USER + ")")
	@Parameter(description = "") @Valid
	public ResponseEntity<PagedConsentList> searchMyConsents(OffsetDateTime acceptDateMin, OffsetDateTime acceptDateMax, OffsetDateTime expirationDateMin, OffsetDateTime expirationDateMax, List<UUID> owners, List<UUID> treatments, Integer offset, Integer limit, String order) throws Exception {
		ConsentSearchCriteria searchCriteria = new ConsentSearchCriteria()
				.treatmentOwnerUuids(owners)
				.treatmentUuids(treatments)
				.acceptDateMin(acceptDateMin)
				.acceptDateMax(acceptDateMax)
				.expirationDateMin(expirationDateMin)
				.expirationDateMax(expirationDateMax);
		val pageable = utilPageable.getPageable(offset, limit, order);
		return ResponseEntity.ok(myConsentsService.searchMyConsents(searchCriteria, pageable));
	}
}
