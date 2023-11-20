package org.rudi.microservice.konsent.service.consent.replacer;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.mapper.data.DictionaryEntryMapper;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class TitleTransientDtoTestReplacer extends DictionaryEntrySetTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final DictionaryEntryMapper dictionaryEntryMapper;

	public TitleTransientDtoTestReplacer(DictionaryEntryDao dictionaryEntryDao, DictionaryEntryMapper dictionaryEntryMapper) {
		super(dictionaryEntryDao);
		this.dictionaryEntryMapper = dictionaryEntryMapper;
	}

	@Override
	public void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException {
		val labelsSet = convertListToSet(dictionaryEntryMapper.dtoToEntities(treatmentVersion.getTitles()));
		treatmentVersion.setDatas(dictionaryEntryMapper.entitiesToDto(saveLabels(labelsSet)));
	}
}
