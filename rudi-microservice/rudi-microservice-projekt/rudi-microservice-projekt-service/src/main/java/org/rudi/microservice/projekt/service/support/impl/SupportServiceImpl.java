package org.rudi.microservice.projekt.service.support.impl;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Support;
import org.rudi.microservice.projekt.core.bean.SupportSearchCriteria;
import org.rudi.microservice.projekt.service.mapper.SupportMapper;
import org.rudi.microservice.projekt.service.support.SupportService;
import org.rudi.microservice.projekt.service.support.impl.validator.SupportValidator;
import org.rudi.microservice.projekt.service.support.impl.validator.UpdateSupportValidator;
import org.rudi.microservice.projekt.storage.dao.support.SupportCustomDao;
import org.rudi.microservice.projekt.storage.dao.support.SupportDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
@Transactional
public class SupportServiceImpl implements SupportService {

	private final List<UpdateSupportValidator> updateSupportValidators;
	private final SupportMapper supportMapper;
	private final SupportDao supportDao;
	private final SupportCustomDao supportCustomDao;

	@Override
	public Page<Support> searchSupports(SupportSearchCriteria searchCriteria, Pageable pageable) {
		return supportMapper.entitiesToDto(supportCustomDao.searchSupports(searchCriteria, pageable), pageable);
	}

	@Override
	public @Nonnull Support getSupport(UUID uuid) {
		return supportMapper.entityToDto(supportDao.findByUUID(uuid));
	}

	@Override
	@Transactional // readOnly = false
	public Support createSupport(Support support) {
		assignReadOnlyFields(support);

		val entity = supportMapper.dtoToEntity(support);
		val savedEntity = supportDao.save(entity);
		return supportMapper.entityToDto(savedEntity);
	}

	private void assignReadOnlyFields(Support support) {
		support.setUuid(UUID.randomUUID());
	}

	@Override
	@Transactional // readOnly = false
	public Support updateSupport(Support support) throws AppServiceException {
		validateUpdate(support);

		final var entity = supportDao.findByUUID(support.getUuid());

		supportMapper.dtoToEntity(support, entity);
		supportDao.save(entity);

		return supportMapper.entityToDto(entity);
	}

	private void validateUpdate(Support support) throws AppServiceException {
		for (final SupportValidator validator : updateSupportValidators) {
			validator.validate(support);
		}
	}

	@Override
	@Transactional // readOnly = false
	public void deleteSupport(UUID uuid) {
		val entity = supportDao.findByUUID(uuid);
		supportDao.delete(entity);
	}

}
