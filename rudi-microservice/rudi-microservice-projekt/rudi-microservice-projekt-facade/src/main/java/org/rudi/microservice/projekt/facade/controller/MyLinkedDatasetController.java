package org.rudi.microservice.projekt.facade.controller;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.microservice.projekt.core.bean.LinkedDatasetSearchCriteria;
import org.rudi.microservice.projekt.core.bean.PagedLinkedDatasetList;
import org.rudi.microservice.projekt.facade.controller.api.MyLinkedDatasetsApi;
import org.rudi.microservice.projekt.service.project.LinkedDatasetService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MyLinkedDatasetController implements MyLinkedDatasetsApi {

	private final LinkedDatasetService linkedDatasetService;
	private final UtilPageable utilPageable;

	@Override
	public ResponseEntity<PagedLinkedDatasetList> searchMyLinkedDatasets(LinkedDatasetSearchCriteria criteria) throws Exception {
		Pageable pageable = utilPageable.getPageable(criteria.getOffset(), criteria.getLimit(), criteria.getOrder());
		return ResponseEntity.ok(linkedDatasetService.searchMyLinkedDatasets(criteria, pageable));
	}
}
