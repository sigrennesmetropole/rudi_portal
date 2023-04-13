package org.rudi.microservice.konsent.service.consent.replacer;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.core.bean.TypologyTreatment;
import org.rudi.microservice.konsent.service.mapper.treatmentversion.TypologyTreatmentMapper;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.TypologyTreatmentDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TypologyTreatmentEntity;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class TypologyTreatmentTransientDtoReplacer extends DictionaryEntrySetTransientDtoReplacer implements TransientDtoReplacer {
	private final TypologyTreatmentMapper typologyTreatmentMapper;
	private final TypologyTreatmentDao typologyTreatmentDao;

	public TypologyTreatmentTransientDtoReplacer(DictionaryEntryDao dictionaryEntryDao, TypologyTreatmentMapper typologyTreatmentMapper, TypologyTreatmentDao typologyTreatmentDao) {
		super(dictionaryEntryDao);
		this.typologyTreatmentMapper = typologyTreatmentMapper;
		this.typologyTreatmentDao = typologyTreatmentDao;
	}

	@Override
	public void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException {
		final TypologyTreatment typologyTreatment = treatmentVersion.getTypology();
		boolean typologyExist = false;
		UUID finalUuid = typologyTreatment.getUuid();
		if (typologyTreatment != null) {
			typologyExist = typologyTreatmentDao.findByUuid(typologyTreatment.getUuid()) != null;
		}
		if (!typologyExist && typologyTreatment != null) {
			TypologyTreatmentEntity typologyTreatmentEntity = typologyTreatmentMapper.dtoToEntity(typologyTreatment);
			val savedTypologyEntity = typologyTreatmentDao.save(typologyTreatmentEntity);
			finalUuid = savedTypologyEntity.getUuid();
			saveLabels(savedTypologyEntity.getLabels());
		}

		if (typologyTreatment != null) {
			treatmentVersion.setTypology(
					typologyTreatmentMapper.entityToDto(typologyTreatmentDao.findByUuid(finalUuid))
			);
		}
	}
}
