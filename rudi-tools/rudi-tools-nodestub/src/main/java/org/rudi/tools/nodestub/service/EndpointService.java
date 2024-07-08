/**
 * RUDI Portail
 */
package org.rudi.tools.nodestub.service;

import java.util.UUID;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.service.exception.AppServiceException;

/**
 * @author FNI18300
 *
 */
public interface EndpointService {

	DocumentContent callEndpoint(UUID mediaUuid) throws AppServiceException;
}
