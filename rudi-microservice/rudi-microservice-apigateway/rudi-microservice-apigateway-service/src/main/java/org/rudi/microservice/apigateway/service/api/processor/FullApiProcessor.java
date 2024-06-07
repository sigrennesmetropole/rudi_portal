/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.service.api.processor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiParameter;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.rudi.microservice.apigateway.service.mapper.ApiParameterMapper;
import org.rudi.microservice.apigateway.storage.dao.throttling.ThrottlingDao;
import org.rudi.microservice.apigateway.storage.entity.api.ApiEntity;
import org.rudi.microservice.apigateway.storage.entity.api.ApiParameterEntity;
import org.rudi.microservice.apigateway.storage.entity.throttling.ThrottlingEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Component
@RequiredArgsConstructor
public class FullApiProcessor implements ApiProcessor {

	private final ThrottlingDao throttlingDao;

	private final ApiParameterMapper apiParameterMapper;

	@Override
	public void processBeforeCreate(ApiEntity entity, Api dto) throws AppServiceException {
		entity.setUuid(UUID.randomUUID());
		processCommonThrottlings(entity, dto);
		processCommonParameters(entity, dto);
	}

	@Override
	public void processBeforeCreate(ApiEntity entity, Api dto, Map<String, Object> context) throws AppServiceException {
		processBeforeCreate(entity, dto);
	}

	@Override
	public void processBeforeUpdate(ApiEntity entity, Api dto) throws AppServiceException {
		processCommonThrottlings(entity, dto);
		processCommonParameters(entity, dto);
	}

	protected void processCommonThrottlings(ApiEntity entity, Api dto) {
		if (CollectionUtils.isNotEmpty(dto.getThrottlings())) {
			processCommonAddThrottlings(entity, dto);
			processCommonRemoveThrottlings(entity, dto);
		} else if (entity.getThrottlings() != null) {
			entity.getThrottlings().clear();
		}
	}

	private void processCommonRemoveThrottlings(ApiEntity entity, Api dto) {
		// on supprime ceux en trop
		Iterator<ThrottlingEntity> it = entity.getThrottlings().iterator();
		while (it.hasNext()) {
			ThrottlingEntity item = it.next();
			if (lookupThrottlingByUuid(dto.getThrottlings(), item.getUuid()) == null) {
				it.remove();
			}
		}
	}

	private void processCommonAddThrottlings(ApiEntity entity, Api dto) {
		// on ajoute ceux qui manque
		for (Throttling item : dto.getThrottlings()) {
			ThrottlingEntity throttling = entity.lookupThrottlingByUuid(item.getUuid());
			if (throttling == null) {
				throttling = throttlingDao.findNullableByUuid(item.getUuid());
				entity.addThrottling(throttling);
			}
		}
	}

	protected Throttling lookupThrottlingByUuid(List<Throttling> throttlings, UUID throttlingUuid) {
		Throttling result = null;
		if (CollectionUtils.isNotEmpty(throttlings)) {
			result = throttlings.stream().filter(t -> t.getUuid().equals(throttlingUuid)).findFirst().orElse(null);
		}
		return result;
	}

	/**
	 * 
	 * @param entity l'entité
	 * @param dto    le dto
	 */
	protected void processCommonParameters(ApiEntity entity, Api dto) {
		if (CollectionUtils.isNotEmpty(dto.getParameters())) {
			prcoessCommonAddParameters(entity, dto);
			processCommonRemoveParameters(entity, dto);
		} else if (entity.getParameters() != null) {
			entity.getParameters().clear();
		}
	}

	private void processCommonRemoveParameters(ApiEntity entity, Api dto) {
		// on supprime ceux en trop
		Iterator<ApiParameterEntity> it = entity.getParameters().iterator();
		while (it.hasNext()) {
			ApiParameterEntity item = it.next();
			if (lookupParameterByUuid(dto.getParameters(), item.getUuid()) == null) {
				it.remove();
			}
		}
	}

	private void prcoessCommonAddParameters(ApiEntity entity, Api dto) {
		for (ApiParameter item : dto.getParameters()) {
			if (item.getUuid() == null) {
				ApiParameterEntity apiParameter = apiParameterMapper.dtoToEntity(item);
				apiParameter.setUuid(UUID.randomUUID());
				item.setUuid(apiParameter.getUuid());
				entity.addParameter(apiParameter);
			} else {
				ApiParameterEntity apiParameter = entity.lookupParameterByUuid(item.getUuid());
				if (apiParameter != null) {
					apiParameterMapper.dtoToEntity(item, apiParameter);
				}
			}
		}
	}

	protected ApiParameter lookupParameterByUuid(List<ApiParameter> parameters, UUID parameterUuid) {
		ApiParameter result = null;
		if (CollectionUtils.isNotEmpty(parameters)) {
			result = parameters.stream().filter(t -> t.getUuid().equals(parameterUuid)).findFirst().orElse(null);
		}
		return result;
	}

}
