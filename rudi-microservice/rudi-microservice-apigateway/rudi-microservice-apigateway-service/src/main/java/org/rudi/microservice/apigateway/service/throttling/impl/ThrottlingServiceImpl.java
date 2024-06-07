/**
 * RUDI Portail
 */
package org.rudi.microservice.apigateway.service.throttling.impl;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.apigateway.core.bean.Throttling;
import org.rudi.microservice.apigateway.core.bean.ThrottlingSearchCriteria;
import org.rudi.microservice.apigateway.service.mapper.ThrottlingMapper;
import org.rudi.microservice.apigateway.service.throttling.ThrottlingService;
import org.rudi.microservice.apigateway.service.throttling.validator.ThrottlingValidator;
import org.rudi.microservice.apigateway.storage.dao.throttling.ThrottlingCustomDao;
import org.rudi.microservice.apigateway.storage.dao.throttling.ThrottlingDao;
import org.rudi.microservice.apigateway.storage.entity.throttling.ThrottlingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * @author FNI18300
 *
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThrottlingServiceImpl implements ThrottlingService {

	private final ThrottlingMapper throttlingMapper;
	private final ThrottlingDao throttlingDao;
	private final ThrottlingCustomDao throttlingCustomDao;
	private final ThrottlingValidator validator;

	@Override
	public Page<Throttling> searchThrottlings(ThrottlingSearchCriteria searchCriteria, Pageable pageable) {
		return throttlingMapper.entitiesToDto(throttlingCustomDao.searchThrottlings(searchCriteria, pageable),
				pageable);
	}

	@Override
	public Throttling getThrottling(UUID uuid) throws AppServiceException {
		ThrottlingEntity entity = getThrottlingEntity(uuid);
		return throttlingMapper.entityToDto(entity);
	}

	@Override
	@Transactional // readOnly = false
	public Throttling createThrottling(Throttling throttling) throws AppServiceException {
		validator.validateCreate(throttling);
		ThrottlingEntity entity = throttlingMapper.dtoToEntity(throttling);
		entity.setUuid(UUID.randomUUID());

		ThrottlingEntity savedEntity = throttlingDao.save(entity);
		return throttlingMapper.entityToDto(savedEntity);
	}

	@Override
	@Transactional // readOnly = false
	public Throttling updateThrottling(Throttling throttling) throws AppServiceException {
		validator.validateUpdate(throttling);
		ThrottlingEntity entity = getThrottlingEntity(throttling.getUuid());
		throttlingMapper.dtoToEntity(throttling, entity);

		throttlingDao.save(entity);
		return throttlingMapper.entityToDto(entity);
	}

	private ThrottlingEntity getThrottlingEntity(UUID throttlingUuid) {
		ThrottlingEntity entity = throttlingDao.findByUUID(throttlingUuid);
		if (entity == null) {
			throw new IllegalArgumentException("Resource inexistante:" + throttlingUuid);
		}
		return entity;
	}

	@Override
	@Transactional // readOnly = false
	public void deleteThrottling(UUID uuid) throws AppServiceException {
		ThrottlingEntity entity = getThrottlingEntity(uuid);
		throttlingDao.delete(entity);
	}

}
