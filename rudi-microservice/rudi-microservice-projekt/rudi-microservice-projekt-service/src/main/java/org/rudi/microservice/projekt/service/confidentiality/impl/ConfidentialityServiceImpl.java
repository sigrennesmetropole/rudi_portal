package org.rudi.microservice.projekt.service.confidentiality.impl;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.core.bean.Confidentiality;
import org.rudi.microservice.projekt.core.bean.ConfidentialitySearchCriteria;
import org.rudi.microservice.projekt.service.confidentiality.ConfidentialityService;
import org.rudi.microservice.projekt.service.confidentiality.impl.validator.ConfidentialityValidator;
import org.rudi.microservice.projekt.service.confidentiality.impl.validator.UpdateConfidentialityValidator;
import org.rudi.microservice.projekt.service.mapper.ConfidentialityMapper;
import org.rudi.microservice.projekt.storage.dao.confidentiality.ConfidentialityCustomDao;
import org.rudi.microservice.projekt.storage.dao.confidentiality.ConfidentialityDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfidentialityServiceImpl implements ConfidentialityService {

	private final List<UpdateConfidentialityValidator> updateConfidentialityValidators;
	private final ConfidentialityMapper confidentialityMapper;
	private final ConfidentialityDao confidentialityDao;
	private final ConfidentialityCustomDao confidentialityCustomDao;
	private final ConfidentialityHelper confidentialityHelper;

	@Override
	public Page<Confidentiality> searchConfidentialities(ConfidentialitySearchCriteria searchCriteria, Pageable pageable) {
		return confidentialityMapper.entitiesToDto(confidentialityCustomDao.searchConfidentialities(searchCriteria, pageable), pageable);
	}

	@Override
	public Confidentiality getConfidentiality(UUID uuid) {
		return confidentialityMapper.entityToDto(confidentialityDao.findByUUID(uuid));
	}

	@Override
	@Transactional // readOnly = false
	public Confidentiality createConfidentiality(Confidentiality confidentiality) {
		assignReadOnlyFields(confidentiality);

		val entity = confidentialityMapper.dtoToEntity(confidentiality);
		val savedEntity = confidentialityDao.save(entity);
		return confidentialityMapper.entityToDto(savedEntity);
	}

	private void assignReadOnlyFields(Confidentiality confidentiality) {
		confidentiality.setUuid(UUID.randomUUID());
	}

	@Override
	@Transactional // readOnly = false
	public Confidentiality updateConfidentiality(Confidentiality confidentiality) throws AppServiceException {
		validateUpdate(confidentiality);

		final var entity = confidentialityDao.findByUUID(confidentiality.getUuid());

		confidentialityMapper.dtoToEntity(confidentiality, entity);
		confidentialityDao.save(entity);

		return confidentialityMapper.entityToDto(entity);
	}

	private void validateUpdate(Confidentiality confidentiality) throws AppServiceException {
		for (final ConfidentialityValidator validator : updateConfidentialityValidators) {
			validator.validate(confidentiality);
		}
	}

	@Override
	@Transactional // readOnly = false
	public void deleteConfidentiality(UUID uuid) {
		val entity = confidentialityDao.findByUUID(uuid);
		confidentialityDao.delete(entity);
	}

	@Override
	public Confidentiality getConfidentialityByCode(String code) {
		return confidentialityMapper.entityToDto(confidentialityDao.findByCode(code));
	}
}
