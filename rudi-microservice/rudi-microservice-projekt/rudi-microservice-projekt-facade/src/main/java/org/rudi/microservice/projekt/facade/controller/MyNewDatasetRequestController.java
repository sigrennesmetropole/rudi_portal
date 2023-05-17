package org.rudi.microservice.projekt.facade.controller;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.projekt.core.bean.NewDatasetRequestSearchCriteria;
import org.rudi.microservice.projekt.core.bean.PagedNewDatasetRequestList;
import org.rudi.microservice.projekt.facade.controller.api.MyNewDatasetRequestsApi;
import org.rudi.microservice.projekt.service.project.NewDatasetRequestService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyNewDatasetRequestController implements MyNewDatasetRequestsApi {

	private final NewDatasetRequestService newDatasetRequestService;
	private final UtilPageable utilPageable;

	@Override
	public ResponseEntity<PagedNewDatasetRequestList> searchMyNewDatasetRequests(NewDatasetRequestSearchCriteria criteria) throws Exception {
		Pageable pageable = utilPageable.getPageable(criteria.getOffset(), criteria.getLimit(), criteria.getOrder());
		return ResponseEntity.ok(newDatasetRequestService.searchMyNewDatasetRequests(criteria, pageable));
	}
}
