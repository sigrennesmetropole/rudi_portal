package org.rudi.microservice.projekt.service.territory.impl;

import java.util.List;
import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.TerritorialScale;
import org.rudi.microservice.projekt.core.bean.TerritorialScaleSearchCriteria;
import org.rudi.microservice.projekt.service.mapper.TerritorialScaleMapper;
import org.rudi.microservice.projekt.service.territory.TerritorialScaleService;
import org.rudi.microservice.projekt.service.territory.impl.validator.TerritorialScaleValidator;
import org.rudi.microservice.projekt.service.territory.impl.validator.UpdateTerritorialScaleValidator;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleCustomDao;
import org.rudi.microservice.projekt.storage.dao.territory.TerritorialScaleDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TerritorialScaleServiceImpl implements TerritorialScaleService {

	private final List<UpdateTerritorialScaleValidator> updateTerritorialScaleValidators;
	private final TerritorialScaleMapper territorialScaleMapper;
	private final TerritorialScaleDao territorialScaleDao;
	private final TerritorialScaleCustomDao territorialScaleCustomDao;

	@Override
	public Page<TerritorialScale> searchTerritorialScales(TerritorialScaleSearchCriteria searchCriteria,
			Pageable pageable) {
		return territorialScaleMapper
				.entitiesToDto(territorialScaleCustomDao.searchTerritorialScales(searchCriteria, pageable), pageable);
	}

	@Override
	public TerritorialScale getTerritorialScale(UUID uuid) {
		return territorialScaleMapper.entityToDto(territorialScaleDao.findByUUID(uuid));
	}

	@Override
	@Transactional // readOnly = false
	public TerritorialScale createTerritorialScale(TerritorialScale territorialScale) {
		assignReadOnlyFields(territorialScale);

		val entity = territorialScaleMapper.dtoToEntity(territorialScale);
		val savedEntity = territorialScaleDao.save(entity);
		return territorialScaleMapper.entityToDto(savedEntity);
	}

	private void assignReadOnlyFields(TerritorialScale territorialScale) {
		territorialScale.setUuid(UUID.randomUUID());
	}

	@Override
	@Transactional // readOnly = false
	public TerritorialScale updateTerritorialScale(TerritorialScale territorialScale) throws AppServiceException {
		validateUpdate(territorialScale);

		final var entity = territorialScaleDao.findByUUID(territorialScale.getUuid());

		territorialScaleMapper.dtoToEntity(territorialScale, entity);
		territorialScaleDao.save(entity);

		return territorialScaleMapper.entityToDto(entity);
	}

	private void validateUpdate(TerritorialScale territorialScale) throws AppServiceException {
		for (final TerritorialScaleValidator validator : updateTerritorialScaleValidators) {
			validator.validate(territorialScale);
		}
	}

	@Override
	@Transactional // readOnly = false
	public void deleteTerritorialScale(UUID uuid) {
		val entity = territorialScaleDao.findByUUID(uuid);
		territorialScaleDao.delete(entity);
	}

}
