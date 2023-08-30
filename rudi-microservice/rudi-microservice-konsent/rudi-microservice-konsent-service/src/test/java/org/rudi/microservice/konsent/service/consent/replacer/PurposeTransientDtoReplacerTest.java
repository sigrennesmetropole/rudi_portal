package org.rudi.microservice.konsent.service.consent.replacer;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.Purpose;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.mapper.treatmentversion.PurposeMapper;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.PurposeDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.PurposeEntity;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class PurposeTransientDtoReplacerTest extends DictionaryEntrySetTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final PurposeMapper purposeMapper;
	private final PurposeDao purposeDao;

	public PurposeTransientDtoReplacerTest(DictionaryEntryDao dictionaryEntryDao, PurposeMapper purposeMapper, PurposeDao purposeDao) {
		super(dictionaryEntryDao);
		this.purposeMapper = purposeMapper;
		this.purposeDao = purposeDao;
	}


	@Override
	public void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException {
		final Purpose purpose = treatmentVersion.getPurpose();
		boolean purposeExist = false;
		UUID finalUuid = purpose.getUuid();
		if (purpose != null) {
			purposeExist = purposeDao.findByUuid(finalUuid) != null;
		}

		if (!purposeExist && purpose != null) {
			PurposeEntity purposeEntity = purposeMapper.dtoToEntity(purpose);
			purposeEntity.setUuid(UUID.randomUUID());
			val savedPurposeEntity = purposeDao.save(purposeEntity);
			finalUuid = savedPurposeEntity.getUuid();
			saveLabels(savedPurposeEntity.getLabels());
		}

		if (purpose != null) {
			treatmentVersion.setPurpose(
					purposeMapper.entityToDto(purposeDao.findByUuid(finalUuid))
			);
		}
	}
}
