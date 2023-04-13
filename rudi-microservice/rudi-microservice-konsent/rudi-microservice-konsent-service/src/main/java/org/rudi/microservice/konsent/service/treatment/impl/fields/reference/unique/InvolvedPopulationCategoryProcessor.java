package org.rudi.microservice.konsent.service.treatment.impl.fields.reference.unique;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.CreateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.UpdateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.TransientEntitiesReplacer;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.InvolvedPopulationCategoryDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.InvolvedPopulationCategoryEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TypologyTreatmentEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvolvedPopulationCategoryProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final InvolvedPopulationCategoryDao populationCategoryDao;
	private final InvolvedPopulationCategoryEntityReplacer populationCategoryEntityReplacer = new InvolvedPopulationCategoryEntityReplacer();

	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) {
			return;
		}
		populationCategoryEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}

	private class InvolvedPopulationCategoryEntityReplacer extends TransientEntitiesReplacer<TreatmentVersionEntity, InvolvedPopulationCategoryEntity> {

		public InvolvedPopulationCategoryEntityReplacer() {
			super(TreatmentVersionEntity::getInvolvedPopulation, TreatmentVersionEntity::setInvolvedPopulation);
		}

		@Nullable
		@Override
		protected InvolvedPopulationCategoryEntity getPersistentEntities(@Nullable InvolvedPopulationCategoryEntity transientPopulationCategoryEntity) throws AppServiceException {
			if (transientPopulationCategoryEntity == null) {
				return null;
			}

			if (transientPopulationCategoryEntity.getUuid() == null) {
				throw new MissingParameterException("involved_population.uuid manquant");
			}

			final InvolvedPopulationCategoryEntity existingPopulationCategoryEntity;
			try {
				existingPopulationCategoryEntity = populationCategoryDao.findByUuid(transientPopulationCategoryEntity.getUuid());
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(transientPopulationCategoryEntity, e);
			}

			if(existingPopulationCategoryEntity == null) {
				throw new AppServiceNotFoundException(InvolvedPopulationCategoryEntity.class, transientPopulationCategoryEntity.getUuid());
			}

			return existingPopulationCategoryEntity;
		}
	}
}
