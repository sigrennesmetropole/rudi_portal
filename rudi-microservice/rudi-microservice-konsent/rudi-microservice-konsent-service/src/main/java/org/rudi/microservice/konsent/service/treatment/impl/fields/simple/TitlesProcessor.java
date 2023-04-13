package org.rudi.microservice.konsent.service.treatment.impl.fields.simple;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.CreateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.TypeDictionaryEntrySetProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.UpdateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.storage.dao.data.DictionaryEntryDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.stereotype.Component;

@Component
public class TitlesProcessor extends TypeDictionaryEntrySetProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final TitlesEntityReplacer titlesEntityReplacer = new TitlesEntityReplacer();

	public TitlesProcessor(DictionaryEntryDao dictionaryEntryDao) {
		super(dictionaryEntryDao);
	}

	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) {
			return;
		}
		titlesEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}

	private class TitlesEntityReplacer extends TypeDictionaryEntrySetEntityReplacer {

		public TitlesEntityReplacer() {
			super(TreatmentVersionEntity::getTitles, TreatmentVersionEntity::setTitles);
		}
	}
}
