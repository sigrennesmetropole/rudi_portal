package org.rudi.microservice.selfdata.service.selfdata;

import java.io.IOException;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;

public interface MatchingService {
	UUID getMatchingToken(UUID datasetUuid, String login) throws IOException, AppServiceNotFoundException;

	boolean hasMatchingToDataset(UUID userUuid, UUID datasetUuid) throws AppServiceUnauthorizedException;
}
