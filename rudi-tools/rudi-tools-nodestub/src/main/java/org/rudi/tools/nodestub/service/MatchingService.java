package org.rudi.tools.nodestub.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.tools.nodestub.bean.MatchingDescription;
import org.rudi.tools.nodestub.bean.MatchingField;

public interface MatchingService {
	MatchingDescription createMatchingToken(UUID datasetUuid, String login, List<MatchingField> matchingFields) throws AppServiceUnauthorizedException, IOException, AppServiceBadRequestException, AppServiceNotFoundException;

	MatchingTokenMetadata getMatchingTokenMetadata(String token) throws IOException, AppServiceNotFoundException;
}
