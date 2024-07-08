package org.rudi.tools.nodestub.service.matching;

import java.util.List;
import java.util.UUID;

import org.rudi.tools.nodestub.bean.MatchingField;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class MatchingRequest {
	private final UUID datasetUuid;
	private final List<MatchingField> matchingFields;
}
