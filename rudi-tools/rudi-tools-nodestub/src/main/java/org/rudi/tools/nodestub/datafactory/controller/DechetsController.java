package org.rudi.tools.nodestub.datafactory.controller;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceForbiddenException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.common.service.helper.UtilContextHelper;
import org.rudi.tools.nodestub.component.helper.MatchingDataHelper;
import org.rudi.tools.nodestub.datafactory.apirecette.bean.BarChartData;
import org.rudi.tools.nodestub.datafactory.apirecette.bean.GenericDataObject;
import org.rudi.tools.nodestub.datafactory.apirecette.controller.api.ApiApi;
import org.rudi.tools.nodestub.datafactory.service.DechetsService;
import org.rudi.tools.nodestub.service.matching.MatchingService;
import org.rudi.tools.nodestub.service.matching.MatchingTokenMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
class DechetsController implements ApiApi {

	private final DechetsService dechetsService;
	private final MatchingService matchingService;
	private final UtilContextHelper utilContextHelper;
	private final MatchingDataHelper matchingDataHelper;

	@Override
	public ResponseEntity<GenericDataObject> gdataTypeGet(String selfdataToken, String type) throws Exception {
		final String idRva = getIdRva(selfdataToken);
		return ResponseEntity.ok(dechetsService.gdataTypeGet(idRva, type));
	}

	@Override
	public ResponseEntity<BarChartData> peseesGet(String selfdataToken, OffsetDateTime maxDate, OffsetDateTime minDate)
			throws Exception {
		final String idRva = getIdRva(selfdataToken);
		return ResponseEntity.ok(dechetsService.peseesGet(idRva, maxDate, minDate));
	}

	private String getIdRva(String selfdataToken) throws IOException, AppServiceForbiddenException,
			AppServiceBadRequestException, AppServiceUnauthorizedException {
		final var matchingTokenMetadata = getMatchingTokenMetadata(selfdataToken);
		final var rvaAddressMatchingField = matchingDataHelper
				.lookupIdRva(matchingTokenMetadata.getRequest().getMatchingFields());
		if (rvaAddressMatchingField != null) {
			return rvaAddressMatchingField.getValue();
		} else {
			return null;
		}
	}

	@Nonnull
	private MatchingTokenMetadata getMatchingTokenMetadata(String selfdataToken)
			throws IOException, AppServiceForbiddenException, AppServiceUnauthorizedException {
		try {
			final var matchingTokenMetadata = matchingService.getMatchingTokenMetadata(selfdataToken);
			checkLogin(matchingTokenMetadata);
			return matchingTokenMetadata;
		} catch (AppServiceNotFoundException e) {
			log.error("Selfdata token metadata not found for token " + selfdataToken, e);
			throw new AppServiceForbiddenException("Access forbidden for selfdata token " + selfdataToken);
		}
	}

	private void checkLogin(MatchingTokenMetadata matchingTokenMetadata)
			throws AppServiceUnauthorizedException, AppServiceForbiddenException {
		final var authenticatedUser = utilContextHelper.getAuthenticatedUser();
		if (authenticatedUser == null) {
			throw new AppServiceUnauthorizedException("Cannot check login without being authenticated.");
		}

		final var authenticatedUserLogin = authenticatedUser.getLogin();
		if (!Objects.equals(matchingTokenMetadata.getLogin(), authenticatedUserLogin)) {
			throw new AppServiceForbiddenException("Access forbidden for user with login " + authenticatedUserLogin);
		}
	}
}
