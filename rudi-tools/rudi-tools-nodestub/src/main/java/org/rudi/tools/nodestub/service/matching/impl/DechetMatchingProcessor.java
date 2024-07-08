/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.service.matching.impl;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.tools.nodestub.bean.MatchingDescription;
import org.rudi.tools.nodestub.bean.MatchingField;
import org.rudi.tools.nodestub.component.helper.MatchingDataHelper;
import org.rudi.tools.nodestub.config.NodeStubConfiguration;
import org.rudi.tools.nodestub.datafactory.service.DechetsService;
import org.rudi.tools.nodestub.service.matching.MatchingProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Component
@RequiredArgsConstructor
public class DechetMatchingProcessor implements MatchingProcessor {

	private final MatchingDataHelper matchingDataHelper;

	private final NodeStubConfiguration nodeStubConfiguration;

	private final DechetsService dechetsService;

	@Override
	public boolean accept(UUID datasetUuid, String login, List<MatchingField> matchingFields)
			throws AppServiceException {
		// on ne traite acutellement que le cas du jdd de gestion des dechets
		return datasetUuid.equals(nodeStubConfiguration.getWasteDatasetUuid())
				&& matchingDataHelper.lookupIdRva(matchingFields) != null;
	}

	@Override
	public MatchingDescription computeToken(UUID datasetUuid, String login, List<MatchingField> matchingFields)
			throws AppServiceException {
		MatchingField rvaMatchingField = matchingDataHelper.lookupIdRva(matchingFields);
		if (rvaMatchingField != null && dechetsService.validateIdRva(rvaMatchingField.getValue())) {
			MatchingDescription response = new MatchingDescription().token(UUID.randomUUID().toString());

			response.setDatasetUuid(datasetUuid);

			// Bouchon, mais normalement : get my UUID de node provider connu de RUDI
			response.setProviderUuid(nodeStubConfiguration.getNodestubUuid());

			// Bouchon, mais normalement : get UUID du producer à partir de l'uuid du dataset chez moi
			response.setProducerUuid(nodeStubConfiguration.getWasteDatasetProducerUuid());

			/// Bouchon, on dit X jours d'expiration au hasard
			response.setExpirationDate(
					OffsetDateTime.now().plus(Duration.ofDays(nodeStubConfiguration.getMatchingTokenValidityDays())));
			return response;
		}

		return null;
	}

}
