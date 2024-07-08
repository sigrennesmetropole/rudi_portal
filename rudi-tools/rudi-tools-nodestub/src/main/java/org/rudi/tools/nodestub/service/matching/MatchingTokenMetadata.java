package org.rudi.tools.nodestub.service.matching;

import org.rudi.tools.nodestub.bean.MatchingDescription;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public
class MatchingTokenMetadata {
	private final MatchingRequest request;
	private final String login;
	private final MatchingDescription response;
}
