package org.rudi.microservice.selfdata.facade.controller;

import java.util.UUID;

import org.rudi.microservice.selfdata.facade.controller.api.MatchingApi;
import org.rudi.microservice.selfdata.service.selfdata.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSULT;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_KONSULT_ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_SELFDATA_ADMINISTRATOR;

@Controller
@RequiredArgsConstructor
public class MatchingController implements MatchingApi {
	private final MatchingService matchingService;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_SELFDATA_ADMINISTRATOR + ")")
	public ResponseEntity<UUID> getMatchingToken(UUID datasetUuid, String login) throws Exception {
		return ResponseEntity.ok(matchingService.getMatchingToken(datasetUuid, login));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_KONSULT_ADMINISTRATOR + ", " + MODULE_KONSULT + ")")
	public ResponseEntity<Boolean> hasMatchingToDataset(UUID userUuid, UUID datasetUuid) throws Exception {
		return ResponseEntity.ok(matchingService.hasMatchingToDataset(userUuid, datasetUuid));
	}
}
