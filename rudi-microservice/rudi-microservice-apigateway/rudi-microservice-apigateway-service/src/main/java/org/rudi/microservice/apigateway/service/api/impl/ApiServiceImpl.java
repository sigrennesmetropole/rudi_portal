package org.rudi.microservice.apigateway.service.api.impl;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.util.ApplicationContext;
import org.rudi.microservice.apigateway.core.bean.Api;
import org.rudi.microservice.apigateway.core.bean.ApiSearchCriteria;
import org.rudi.microservice.apigateway.service.api.ApiEvent;
import org.rudi.microservice.apigateway.service.api.ApiEventMode;
import org.rudi.microservice.apigateway.service.api.ApiService;
import org.rudi.microservice.apigateway.service.api.processor.ApiProcessor;
import org.rudi.microservice.apigateway.service.api.validator.ApiValidator;
import org.rudi.microservice.apigateway.service.mapper.ApiMapper;
import org.rudi.microservice.apigateway.storage.dao.api.ApiCustomDao;
import org.rudi.microservice.apigateway.storage.dao.api.ApiDao;
import org.rudi.microservice.apigateway.storage.entity.api.ApiEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApiServiceImpl implements ApiService {

	private final ApplicationEventPublisher publisher;

	private final ApiDao apiDao;

	private final ApiCustomDao apiCustomDao;

	private final ApiMapper apiMapper;

	private final ApiValidator validator;

	private final ApiProcessor processor;

	@Override
	public Page<Api> searchApis(ApiSearchCriteria searchCriteria, Pageable pageable) {
		return apiMapper.entitiesToDto(apiCustomDao.searchApis(searchCriteria, pageable), pageable);
	}

	@Override
	public Api getApi(UUID uuid) throws AppServiceException {
		return apiMapper.entityToDto(apiDao.findByUuid(uuid));
	}

	@Override
	public Api createApi(Api api) throws AppServiceException {
		Api result = lookupMe().createSimpleApi(api);
		publisher.publishEvent(new ApiEvent(ApiEventMode.CREATE, result.getUuid()));
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW) // readOnly = false
	public Api createSimpleApi(Api api) throws AppServiceException {
		validator.validateCreate(api);
		ApiEntity entity = apiMapper.dtoToEntity(api);
		processor.processBeforeCreate(entity, api);
		entity.setUuid(UUID.randomUUID());
		checkUnicity(entity);

		ApiEntity savedEntity = apiDao.save(entity);
		return apiMapper.entityToDto(savedEntity);
	}

	@Override
	public Api updateApi(Api api) throws AppServiceException {
		Api result = lookupMe().updateSimpleApi(api);
		publisher.publishEvent(new ApiEvent(ApiEventMode.UPDATE, result.getUuid()));
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW) // readOnly = false
	public Api updateSimpleApi(Api api) throws AppServiceException {
		validator.validateUpdate(api);
		ApiEntity entity = apiDao.findByUuid(api.getUuid());
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + api.getUuid());
		}
		apiMapper.dtoToEntity(api, entity);
		processor.processBeforeUpdate(entity, api);
		apiDao.save(entity);
		return apiMapper.entityToDto(entity);
	}

	@Override
	public void deleteApi(UUID uuid) throws AppServiceException {
		lookupMe().deleteSimpleApi(uuid);
		publisher.publishEvent(new ApiEvent(ApiEventMode.DELETE, uuid));
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW) // readOnly = false
	public void deleteSimpleApi(UUID uuid) throws AppServiceException {
		ApiEntity entity = apiDao.findByUuid(uuid);
		if (entity == null) {
			throw new AppServiceException("Resource inexistante:" + uuid);
		}
		apiDao.delete(entity);
	}

	protected void checkUnicity(ApiEntity api) throws AppServiceException {
		ApiSearchCriteria apiSearchCriteria = new ApiSearchCriteria();
		apiSearchCriteria.setGlobalId(api.getGlobalId());
		apiSearchCriteria.setGlobalId(api.getMediaId());
		Page<ApiEntity> apis = apiCustomDao.searchApis(apiSearchCriteria, Pageable.ofSize(1));
		if (!apis.isEmpty()) {
			long apiCount = apis.getContent().stream().filter(item -> !item.getUuid().equals(api.getUuid())).count();
			if (apiCount > 0) {
				throw new AppServiceException("Api already exists:" + api);
			}
		}
	}

	protected ApiServiceImpl lookupMe() {
		return (ApiServiceImpl) ApplicationContext.getBean(ApiService.class);
	}
}
