package org.rudi.microservice.konsent.service.treatment.impl.fields.reference.unique;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.CreateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.UpdateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.TransientEntitiesReplacer;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.RetentionDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.RetentionEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RetentionProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final RetentionDao retentionDao;
	private final RetentionEntityReplacer retentionEntityReplacer = new RetentionEntityReplacer();

	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) {
			return;
		}
		retentionEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}

	private class RetentionEntityReplacer extends TransientEntitiesReplacer<TreatmentVersionEntity, RetentionEntity> {

		private RetentionEntityReplacer() {
			super(TreatmentVersionEntity::getRetention, TreatmentVersionEntity::setRetention);
		}


		@Nullable
		@Override
		protected RetentionEntity getPersistentEntities(@Nullable RetentionEntity transientEntities) throws AppServiceException {
			if (transientEntities == null) {
				return null;
			}

			if (transientEntities.getUuid() == null) {
				throw new MissingParameterException("retention.uuid manquant");
			}

			final RetentionEntity existingRetentionEntity;
			try {
				existingRetentionEntity = retentionDao.findByUuid(transientEntities.getUuid());
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(transientEntities, e);
			}

			if(existingRetentionEntity == null) {
				throw new AppServiceNotFoundException(RetentionEntity.class, transientEntities.getUuid());
			}

			return existingRetentionEntity;
		}
	}
}
