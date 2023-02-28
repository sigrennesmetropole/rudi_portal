/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.service;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.tools.nodestub.bean.MatchingDescription;
import org.rudi.tools.nodestub.bean.MatchingField;

/**
 * @author FNI18300
 *
 */
public interface MatchingProcessor {

	boolean accept(UUID datasetUuid, String login, List<MatchingField> matchingFields) throws AppServiceException;

	MatchingDescription computeToken(UUID datasetUuid, String login, List<MatchingField> matchingFields)
			throws AppServiceException;

}
