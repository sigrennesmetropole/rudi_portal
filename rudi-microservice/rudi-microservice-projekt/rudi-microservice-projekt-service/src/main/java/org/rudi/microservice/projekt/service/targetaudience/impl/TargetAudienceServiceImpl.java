package org.rudi.microservice.projekt.service.targetaudience.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.TargetAudience;
import org.rudi.microservice.projekt.core.bean.TargetAudienceSearchCriteria;
import org.rudi.microservice.projekt.service.mapper.TargetAudienceMapper;
import org.rudi.microservice.projekt.service.targetaudience.TargetAudienceService;
import org.rudi.microservice.projekt.storage.dao.targetaudience.TargetAudienceCustomDao;
import org.rudi.microservice.projekt.storage.dao.targetaudience.TargetAudienceDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TargetAudienceServiceImpl implements TargetAudienceService {
	private final TargetAudienceMapper targetAudienceMapper;
	private final TargetAudienceDao targetAudienceDao;
	private final TargetAudienceCustomDao targetAudienceCustomDao;

	@Override
	public Page<TargetAudience> searchTargetAudiences(TargetAudienceSearchCriteria searchCriteria, Pageable pageable) {
		return targetAudienceMapper.entitiesToDto(targetAudienceCustomDao.searchTargetAudiences(searchCriteria, pageable), pageable);
	}

	@Override
	public TargetAudience getTargetAudience(UUID uuid) {
		return targetAudienceMapper.entityToDto(targetAudienceDao.findByUUID(uuid));
	}

	@Override
	@Transactional
	public TargetAudience createTargetAudience(TargetAudience targetAudience) throws AppServiceException {
		targetAudience.setUuid(UUID.randomUUID());
		val entity = targetAudienceMapper.dtoToEntity(targetAudience);
		val saved = targetAudienceDao.save(entity);
		return targetAudienceMapper.entityToDto(saved);
	}

	@Override
	@Transactional
	public TargetAudience updateTargetAudience(TargetAudience targetAudience) throws AssertionError {
		assert targetAudience.getUuid() != null;
		final var entity = targetAudienceDao.findByUUID(targetAudience.getUuid());
		targetAudienceMapper.dtoToEntity(targetAudience, entity);
		targetAudienceDao.save(entity);
		return targetAudienceMapper.entityToDto(entity);
	}

	@Override
	@Transactional
	public void deleteTargetAudience(UUID uuid) {
		targetAudienceDao.delete(targetAudienceDao.findByUUID(uuid));
	}
}
