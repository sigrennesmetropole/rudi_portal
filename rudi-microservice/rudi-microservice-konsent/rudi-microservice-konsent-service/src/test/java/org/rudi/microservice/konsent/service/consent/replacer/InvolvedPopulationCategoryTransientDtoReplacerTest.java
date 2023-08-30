package org.rudi.microservice.konsent.service.consent.replacer;

import java.util.UUID;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.core.bean.InvolvedPopulationCategory;
import org.rudi.microservice.konsent.core.bean.TreatmentVersion;
import org.rudi.microservice.konsent.service.mapper.treatmentversion.InvolvedPopulationCategoryMapper;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.InvolvedPopulationCategoryDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.InvolvedPopulationCategoryEntity;
import org.springframework.stereotype.Component;

import lombok.val;

@Component
public class InvolvedPopulationCategoryTransientDtoReplacerTest extends DictionaryEntrySetTransientDtoReplacerTest implements TransientDtoReplacerTest {
	private final InvolvedPopulationCategoryMapper populationCategoryMapper;
	private final InvolvedPopulationCategoryDao populationCategoryDao;

	public InvolvedPopulationCategoryTransientDtoReplacerTest(DictionaryEntryDao dictionaryEntryDao, InvolvedPopulationCategoryMapper populationCategoryMapper, InvolvedPopulationCategoryDao populationCategoryDao) {
		super(dictionaryEntryDao);
		this.populationCategoryMapper = populationCategoryMapper;
		this.populationCategoryDao = populationCategoryDao;
	}

	@Override
	public void replaceDtoFor(TreatmentVersion treatmentVersion) throws AppServiceException {
		final InvolvedPopulationCategory populationCategory = treatmentVersion.getInvolvedPopulation();
		UUID finalUuid = populationCategory.getUuid();
		boolean categoryExist = populationCategoryDao.findByUuid(populationCategory.getUuid()) != null;

		if (!categoryExist && populationCategory != null) {
			InvolvedPopulationCategoryEntity populationCategoryEntity = populationCategoryMapper.dtoToEntity(populationCategory);
			val savedPopulationCategory = populationCategoryDao.save(populationCategoryEntity);
			finalUuid = savedPopulationCategory.getUuid();
			saveLabels(savedPopulationCategory.getLabels());
		}

		if (populationCategory != null) {
			treatmentVersion.setInvolvedPopulation(
					populationCategoryMapper.entityToDto(populationCategoryDao.findByUuid(finalUuid))
			);
		}
	}
}
