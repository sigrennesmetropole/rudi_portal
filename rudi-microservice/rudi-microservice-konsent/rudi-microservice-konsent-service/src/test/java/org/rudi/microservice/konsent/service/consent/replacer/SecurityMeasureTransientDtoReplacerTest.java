package org.rudi.microservice.konsent.service.consent.replacer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.SecurityMeasure;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.mapper.treatmentversion.SecurityMeasureMapper;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.SecurityMeasureDao;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class SecurityMeasureTransientDtoReplacerTest extends DictionaryEntrySetTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final SecurityMeasureDao securityMeasureDao;
	private final SecurityMeasureMapper securityMeasureMapper;

	public SecurityMeasureTransientDtoReplacerTest(DictionaryEntryDao dictionaryEntryDao, SecurityMeasureDao securityMeasureDao, SecurityMeasureMapper securityMeasureMapper) {
		super(dictionaryEntryDao);
		this.securityMeasureDao = securityMeasureDao;
		this.securityMeasureMapper = securityMeasureMapper;
	}

	@Override
	public void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException {
		val securityMeasures = treatmentVersion.getSecurityMeasures();
		if (CollectionUtils.isNotEmpty(securityMeasures)) {
			final List<SecurityMeasure> savedSecurityMeasures = new ArrayList<>(securityMeasures.size());
			for (final SecurityMeasure securityMeasure : securityMeasures) {
				val securityMeasureEntity = securityMeasureMapper.dtoToEntity(securityMeasure);
				securityMeasureEntity.setUuid(UUID.randomUUID());
				val savedSecurityMeasureEntity = securityMeasureDao.save(securityMeasureEntity);
				saveLabels(savedSecurityMeasureEntity.getLabels());
				savedSecurityMeasures.add(
						securityMeasureMapper.entityToDto(savedSecurityMeasureEntity)
				);
			}
			treatmentVersion.setSecurityMeasures(savedSecurityMeasures);
		}
	}
//
//	private void saveSecurityMeasureLabels(Set<DictionaryEntryEntity> labels) {
//		for (DictionaryEntryEntity dictionaryEntryEntity : labels) {
//			dictionaryEntryEntity.setUuid(UUID.randomUUID());
//			val savedLabel = dictionaryEntryDao.save(dictionaryEntryEntity);
//		}
//	}
}
