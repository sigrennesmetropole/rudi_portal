/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.component.helper;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.tools.nodestub.bean.MatchingField;
import org.rudi.tools.nodestub.component.NodeStubConstants;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class MatchingDataHelper {

	public MatchingField lookupIdRva(List<MatchingField> matchingFields) throws AppServiceBadRequestException {
		if (CollectionUtils.isEmpty(matchingFields)) {
			return null;
		}
		return matchingFields.stream().filter(
				matchingField -> matchingField.getCode().equals(NodeStubConstants.RVA_ADDRESS_MATCHING_FIELD_CODE))
				.findFirst().orElseThrow(
						() -> new AppServiceBadRequestException(String.format("Cannot find %s matching field from %s",
								NodeStubConstants.RVA_ADDRESS_MATCHING_FIELD_CODE, matchingFields)));
	}

}
