package org.rudi.microservice.konsent.service.treatment.impl.fields.reference.unique;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.CreateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.UpdateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.TransientEntitiesReplacer;
import org.rudi.microservice.konsent.storage.dao.treatmentversion.PurposeDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.PurposeEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.RetentionEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurposeProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final PurposeDao purposeDao;
	private final PurposeEntityReplacer purposeEntityReplacer = new PurposeEntityReplacer();

	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) { // Si on a pas de treatmentVersion alors pas de field a processé
			return;
		}
		purposeEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}

	private class PurposeEntityReplacer extends TransientEntitiesReplacer<TreatmentVersionEntity, PurposeEntity> {

		public PurposeEntityReplacer() {
			super(TreatmentVersionEntity::getPurpose, TreatmentVersionEntity::setPurpose);
		}

		@Nullable
		@Override
		protected PurposeEntity getPersistentEntities(@Nullable PurposeEntity transientEntities) throws AppServiceException {
			if (transientEntities == null) {
				return null;
			}

			if (transientEntities.getUuid() == null) {
				throw new MissingParameterException("purpose.uuid manquant");
			}

			final PurposeEntity existingPurposeEntity;
			try {
				existingPurposeEntity = purposeDao.findByUuid(transientEntities.getUuid());
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(transientEntities, e);
			}

			if(existingPurposeEntity == null) {
				throw new AppServiceNotFoundException(PurposeEntity.class, transientEntities.getUuid());
			}

			return existingPurposeEntity;
		}
	}
}
