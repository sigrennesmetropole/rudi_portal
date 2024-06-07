/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_APIGATEWAY_ADMINISTRATOR;

import java.util.UUID;

import javax.validation.Valid;

import org.rudi.microservice.apigateway.core.bean.PagedThrottlingList;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.rudi.microservice.apigateway.core.bean.ThrottlingSearchCriteria;
import org.rudi.microservice.apigateway.facade.controller.api.ThrottlingsApi;
import org.rudi.microservice.apigateway.facade.util.UtilPageable;
import org.rudi.microservice.apigateway.service.throttling.ThrottlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author FNI18300
 *
 */
@RestController
public class ThrottlingsController implements ThrottlingsApi {

	@Autowired
	private ThrottlingService throttlingService;

	@Autowired
	private UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_APIGATEWAY_ADMINISTRATOR + ")")
	public ResponseEntity<Throttling> createThrottling(@Valid Throttling throttling) throws Exception {
		return ResponseEntity.ok(throttlingService.createThrottling(throttling));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_APIGATEWAY_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteThrottling(UUID throttlingUuid) throws Exception {
		throttlingService.deleteThrottling(throttlingUuid);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Throttling> getThorttling(UUID throttlingUuid) throws Exception {
		return ResponseEntity.ok(throttlingService.getThrottling(throttlingUuid));
	}

	@Override
	public ResponseEntity<PagedThrottlingList> searchThorttlings(@Valid Boolean active, @Valid String code,
			@Valid Integer limit, @Valid Integer offset, @Valid String order) throws Exception {
		PagedThrottlingList result = new PagedThrottlingList();
		ThrottlingSearchCriteria searchCriteria = new ThrottlingSearchCriteria().active(active).code(code);

		Page<Throttling> apis = throttlingService.searchThrottlings(searchCriteria,
				utilPageable.getPageable(offset, limit, order));
		result.setElements(apis.getContent());
		result.setTotal(apis.getTotalElements());
		return ResponseEntity.ok(result);
	}

	@Override
	@PreAuthorize("hasAnyRole(" + MODULE_APIGATEWAY_ADMINISTRATOR + ")")
	public ResponseEntity<Throttling> updateThrottling(@Valid Throttling throttling) throws Exception {
		return ResponseEntity.ok(throttlingService.updateThrottling(throttling));
	}

}
