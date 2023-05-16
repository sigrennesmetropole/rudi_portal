package org.rudi.microservice.selfdata.facade.controller;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.selfdata.core.bean.PagedSelfdataInformationRequestList;
import org.rudi.microservice.selfdata.core.bean.SelfdataInformationRequestSearchCriteria;
import org.rudi.microservice.selfdata.facade.controller.api.MySelfdataInformationRequestsApi;
import org.rudi.microservice.selfdata.service.selfdata.SelfdataService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

@RestController
@RequiredArgsConstructor
public class MySelfdataInformationRequestController implements MySelfdataInformationRequestsApi {

	private final SelfdataService selfdataService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + USER + ")")
	public ResponseEntity<PagedSelfdataInformationRequestList> searchMySelfdataInformationRequests(SelfdataInformationRequestSearchCriteria criteria) throws Exception {
		Pageable pageable = utilPageable.getPageable(criteria.getOffset(), criteria.getLimit(), criteria.getOrder());
		return ResponseEntity.ok(selfdataService.searchMySelfdataInformationRequests(criteria, pageable));
	}
}
