package org.rudi.microservice.konsent.service.treatment.impl.fields.reference.unique;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.CreateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.UpdateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.TransientEntitiesReplacer;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.TypologyTreatmentDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.PurposeEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TypologyTreatmentEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TypologyTreatmentProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final TypologyTreatmentDao typologyDao;
	private final TypologyTreatmentEntityReplacer typologyTreatmentEntityReplacer = new TypologyTreatmentEntityReplacer();

	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) {
			return;
		}
		typologyTreatmentEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}

	private class TypologyTreatmentEntityReplacer extends TransientEntitiesReplacer<TreatmentVersionEntity, TypologyTreatmentEntity> {

		private TypologyTreatmentEntityReplacer() {
			super(TreatmentVersionEntity::getTypology, TreatmentVersionEntity::setTypology);
		}

		@Nullable
		@Override
		protected TypologyTreatmentEntity getPersistentEntities(@Nullable TypologyTreatmentEntity transientEntities) throws AppServiceException {
			if (transientEntities == null) {
				return null;
			}

			if (transientEntities.getUuid() == null) {
				throw new MissingParameterException("typology_treatment.uuid manquant");
			}

			final TypologyTreatmentEntity existingTypologyTreatmentEntity;
			try {
				existingTypologyTreatmentEntity = typologyDao.findByUuid(transientEntities.getUuid());
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(transientEntities, e);
			}

			if(existingTypologyTreatmentEntity == null) {
				throw new AppServiceNotFoundException(TypologyTreatmentEntity.class, transientEntities.getUuid());
			}

			return existingTypologyTreatmentEntity;
		}
	}
}
