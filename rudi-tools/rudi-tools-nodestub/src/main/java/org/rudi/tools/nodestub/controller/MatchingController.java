package org.rudi.tools.nodestub.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.tools.nodestub.bean.MatchingDescription;
import org.rudi.tools.nodestub.bean.MatchingField;
import org.rudi.tools.nodestub.controller.api.MatchingApi;
import org.rudi.tools.nodestub.service.matching.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MatchingController implements MatchingApi {

	private final MatchingService matchingService;

	@Override
	public ResponseEntity<MatchingDescription> createMatchingToken(UUID datasetUuid, String login, List<MatchingField> matchingField) throws AppServiceUnauthorizedException, IOException, AppServiceBadRequestException, AppServiceNotFoundException {
		return ResponseEntity.ok(matchingService.createMatchingToken(datasetUuid, login, matchingField));
	}
}
