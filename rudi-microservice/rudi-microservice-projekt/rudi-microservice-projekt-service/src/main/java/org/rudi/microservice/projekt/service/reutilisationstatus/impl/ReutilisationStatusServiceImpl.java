package org.rudi.microservice.projekt.service.reutilisationstatus.impl;

import java.util.UUID;

import org.rudi.microservice.projekt.core.bean.ReutilisationStatus;
import org.rudi.microservice.projekt.core.bean.ReutilisationStatusSearchCriteria;
import org.rudi.microservice.projekt.service.mapper.ReutilisationStatusMapper;
import org.rudi.microservice.projekt.service.reutilisationstatus.ReutilisationStatusService;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusCustomDao;
import org.rudi.microservice.projekt.storage.dao.reutilisationstatus.ReutilisationStatusDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReutilisationStatusServiceImpl implements ReutilisationStatusService {
	private final ReutilisationStatusDao reutilisationStatusDao;
	private final ReutilisationStatusCustomDao reutilisationStatusCustomDao;
	private final ReutilisationStatusMapper reutilisationStatusMapper;

	@Override
	@Transactional // readonly = false
	public ReutilisationStatus createReutilisationStatus(ReutilisationStatus reutilisationStatus) {

		assignReadOnlyFields(reutilisationStatus);

		val entity = reutilisationStatusMapper.dtoToEntity(reutilisationStatus);
		val savedEntity = reutilisationStatusDao.save(entity);
		return reutilisationStatusMapper.entityToDto(savedEntity);
	}

	@Override
	public ReutilisationStatus getReutilisationStatus(UUID uuid) {
		return reutilisationStatusMapper.entityToDto(reutilisationStatusDao.findByUUID(uuid));
	}

	@Override
	public Page<ReutilisationStatus> searchReutilisationStatus(ReutilisationStatusSearchCriteria criteria,
			Pageable pageable) {
		return reutilisationStatusMapper
				.entitiesToDto(reutilisationStatusCustomDao.searchReutilisationStatus(criteria, pageable), pageable);
	}

	@Override
	@Transactional // readonly = false
	public ReutilisationStatus updateReutilisationStatus(UUID uuid, ReutilisationStatus reutilisationStatus) {
		final var entity = reutilisationStatusDao.findByUUID(uuid);
		reutilisationStatusMapper.dtoToEntity(reutilisationStatus, entity);

		val returnedEntity = reutilisationStatusDao.save(entity);

		return reutilisationStatusMapper.entityToDto(returnedEntity);
	}

	private void assignReadOnlyFields(ReutilisationStatus reutilisationStatus) {
		reutilisationStatus.setUuid(UUID.randomUUID());
	}

	@Override
	public ReutilisationStatus getReutilisationStatusByCode(String code) {
		return reutilisationStatusMapper.entityToDto(reutilisationStatusDao.findByCode(code));
	}
}
