package org.rudi.microservice.selfdata.facade.controller;

import java.time.OffsetDateTime;
import java.util.UUID;


import org.rudi.common.facade.util.UtilPageable;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.selfdata.core.bean.BarChartData;
import org.rudi.microservice.selfdata.core.bean.GenericDataObject;
import org.rudi.microservice.selfdata.core.bean.PagedSelfdataDatasetList;
import org.rudi.microservice.selfdata.core.bean.SelfdataDatasetSearchCriteria;
import org.rudi.microservice.selfdata.facade.controller.api.SelfdataDatasetsApi;
import org.rudi.microservice.selfdata.service.selfdata.SelfdataService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODERATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.USER;

@RestController
@RequiredArgsConstructor
public class SelfdataDatasetController implements SelfdataDatasetsApi {

	private final UtilPageable utilPageable;

	private final SelfdataService selfdataService;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + "," + MODERATOR + "," + USER + ")")
	public ResponseEntity<PagedSelfdataDatasetList> searchSelfdataDatasets(Integer offset, Integer limit, String order) throws AppServiceException {

		SelfdataDatasetSearchCriteria criteria = new SelfdataDatasetSearchCriteria();
		criteria.setOffset(offset);
		criteria.setLimit(limit);
		criteria.setOrder(order);

		Pageable pageable = utilPageable.getPageable(criteria.getOffset(), criteria.getLimit(), criteria.getOrder());

		return ResponseEntity.ok(selfdataService.searchSelfdataDatasets(criteria, pageable));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + USER + ")")
	public ResponseEntity<BarChartData> getTpbcData(UUID uuid, OffsetDateTime minDate, OffsetDateTime maxDate) throws Exception {
		return ResponseEntity.ok(selfdataService.getTpbcData(uuid, minDate, maxDate));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + USER + ")")
	public ResponseEntity<GenericDataObject> getGdataData(UUID uuid) throws Exception {
		return ResponseEntity.ok(selfdataService.getGdataData(uuid));
	}
}
