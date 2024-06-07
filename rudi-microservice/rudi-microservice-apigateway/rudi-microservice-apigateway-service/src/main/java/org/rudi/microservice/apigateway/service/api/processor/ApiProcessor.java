/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.service.api.processor;

import java.util.Map;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.storage.entity.api.ApiEntity;

/**
 * @author FNI18300
 *
 */
public interface ApiProcessor {

	void processBeforeCreate(ApiEntity entity, Api dto) throws AppServiceException;

	void processBeforeCreate(ApiEntity entity, Api dto, Map<String, Object> context) throws AppServiceException;

	void processBeforeUpdate(ApiEntity entity, Api dto) throws AppServiceException;
}
