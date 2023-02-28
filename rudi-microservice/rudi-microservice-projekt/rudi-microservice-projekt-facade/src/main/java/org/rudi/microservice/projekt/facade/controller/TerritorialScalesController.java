package org.rudi.microservice.projekt.facade.controller;

import java.util.UUID;

import org.rudi.common.facade.util.UtilPageable;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.PagedTerritorialScaleList;
import org.rudi.microservice.projekt.core.bean.TerritorialScale;
import org.rudi.microservice.projekt.core.bean.TerritorialScaleSearchCriteria;
import org.rudi.microservice.projekt.facade.controller.api.TerritorialScalesApi;
import org.rudi.microservice.projekt.service.territory.TerritorialScaleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.val;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_PROJEKT;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_PROJEKT_ADMINISTRATOR;

@RestController
@RequiredArgsConstructor
public class TerritorialScalesController implements TerritorialScalesApi {

	private final TerritorialScaleService territorialScaleService;
	private final UtilPageable utilPageable;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_PROJEKT_ADMINISTRATOR + ", " + MODULE_PROJEKT + ")")
	public ResponseEntity<TerritorialScale> createTerritorialScale(TerritorialScale territorialScale) throws AppServiceException {
		val createdTerritorialScale = territorialScaleService.createTerritorialScale(territorialScale);
		val location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{uuid}")
				.buildAndExpand(createdTerritorialScale.getUuid())
				.toUri();
		return ResponseEntity.created(location).body(createdTerritorialScale);
	}

	@Override
	public ResponseEntity<TerritorialScale> getTerritorialScale(UUID uuid) {
		return ResponseEntity.ok(territorialScaleService.getTerritorialScale(uuid));
	}

	@Override
	public ResponseEntity<PagedTerritorialScaleList> searchTerritorialScales(Integer limit, Integer offset, String order) throws Exception {
		val searchCriteria = new TerritorialScaleSearchCriteria();
		val pageable = utilPageable.getPageable(offset, limit, order);
		val page = territorialScaleService.searchTerritorialScales(searchCriteria, pageable);
		return ResponseEntity.ok(new PagedTerritorialScaleList()
				.total(page.getTotalElements())
				.elements(page.getContent()));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_PROJEKT_ADMINISTRATOR + ", " + MODULE_PROJEKT + ")")
	public ResponseEntity<Void> updateTerritorialScale(UUID uuid, TerritorialScale territorialScale) throws Exception {
		territorialScale.setUuid(uuid);
		territorialScaleService.updateTerritorialScale(territorialScale);
		return ResponseEntity.noContent().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_PROJEKT_ADMINISTRATOR + ", " + MODULE_PROJEKT + ")")
	public ResponseEntity<Void> deleteTerritorialScale(UUID uuid) {
		territorialScaleService.deleteTerritorialScale(uuid);
		return ResponseEntity.noContent().build();
	}
}
