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
public class OperationTreatmentNaturesProcessor extends TypeDictionaryEntrySetProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final OperationTreatmentNaturesEntityReplacer operationTreatmentNaturesEntityReplacer = new OperationTreatmentNaturesEntityReplacer();

	public OperationTreatmentNaturesProcessor(DictionaryEntryDao dictionaryEntryDao) {
		super(dictionaryEntryDao);
	}

	// TODO: Trouver comment injecter correctement dans la hierarchie le DictionaryEntryDao
	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) {
			return;
		}
		operationTreatmentNaturesEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}


	private class OperationTreatmentNaturesEntityReplacer extends TypeDictionaryEntrySetEntityReplacer {

		public OperationTreatmentNaturesEntityReplacer() {
			super(TreatmentVersionEntity::getOperationTreatmentNatures, TreatmentVersionEntity::setOperationTreatmentNatures);
		}
	}
}
