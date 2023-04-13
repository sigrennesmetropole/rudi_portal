package org.rudi.microservice.konsent.service.consent.replacer;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.Retention;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.mapper.treatmentversion.RetentionMapper;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.RetentionDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.RetentionEntity;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class RetentionTransientDtoReplacer extends DictionaryEntrySetTransientDtoReplacer implements TransientDtoReplacer {
	private final RetentionMapper retentionMapper;
	private final RetentionDao retentionDao;

	public RetentionTransientDtoReplacer(DictionaryEntryDao dictionaryEntryDao, RetentionMapper retentionMapper, RetentionDao retentionDao) {
		super(dictionaryEntryDao);
		this.retentionMapper = retentionMapper;
		this.retentionDao = retentionDao;
	}

	@Override
	public void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException {
		final Retention retention = treatmentVersion.getRetention();
		boolean retentionExist = false;
		UUID finalUuid = retention.getUuid();
		if (retention != null) {
			retentionExist = retentionDao.findByUuid(retention.getUuid()) != null;
		}
		if (!retentionExist && retention != null) {
			RetentionEntity retentionEntity = retentionMapper.dtoToEntity(retention);
			val savedRetention = retentionDao.save(retentionEntity);
			finalUuid = savedRetention.getUuid();
			saveLabels(savedRetention.getLabels());
		}

		if (retention != null) {
			treatmentVersion.setRetention(
					retentionMapper.entityToDto(retentionDao.findByUuid(finalUuid))
			);
		}
	}
}
