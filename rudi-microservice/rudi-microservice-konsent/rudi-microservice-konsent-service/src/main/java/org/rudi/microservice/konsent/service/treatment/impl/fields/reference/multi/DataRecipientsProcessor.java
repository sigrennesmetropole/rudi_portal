package org.rudi.microservice.konsent.service.treatment.impl.fields.reference.multi;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceNotFoundException;
import org.rudi.common.service.exception.MissingParameterException;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.CreateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.UpdateTreatmentVersionFieldProcessor;
import org.rudi.microservice.konsent.service.treatment.impl.fields.common.TransientEntitiesReplacer;
import org.rudi.microservice.konsent.storage.dao.data.DataRecipientDao;
import org.rudi.microservice.konsent.storage.entity.data.DataRecipientEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.SecurityMeasureEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class DataRecipientsProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final DataRecipientDao dataRecipientDao;
	private final DataRecipientEntityReplacer dataRecipientEntityReplacer = new DataRecipientEntityReplacer();

	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) {
			return;
		}
		dataRecipientEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}

	private class DataRecipientEntityReplacer extends TransientEntitiesReplacer<TreatmentVersionEntity, Set<DataRecipientEntity>> {

		public DataRecipientEntityReplacer() {
			super(TreatmentVersionEntity::getDataRecipients, TreatmentVersionEntity::setDataRecipients);
		}

		@Nullable
		@Override
		protected Set<DataRecipientEntity> getPersistentEntities(@Nullable Set<DataRecipientEntity> dataRecipientEntities) throws AppServiceException {
			if (dataRecipientEntities == null) {
				return null;
			}
			final Set<DataRecipientEntity> existingDataRecipients = new HashSet<>(dataRecipientEntities.size());
			for (final DataRecipientEntity dataRecipient : dataRecipientEntities) {
				final var existingDataRecipient = getExistingDataRecipient(dataRecipient);
				existingDataRecipients.add(existingDataRecipient);
			}
			return existingDataRecipients;
		}

		private DataRecipientEntity getExistingDataRecipient(DataRecipientEntity dataRecipient)
				throws MissingParameterException, AppServiceNotFoundException {
			val uuid = dataRecipient.getUuid();
			if (uuid == null) {
				throw new MissingParameterException("dataRecipient.uuid manquant");
			}

			DataRecipientEntity dataRecipientEntity;
			try {
				dataRecipientEntity = dataRecipientDao.findByUuid(uuid);
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(dataRecipient, e);
			}

			if (dataRecipientEntity == null) {
				throw new AppServiceNotFoundException(DataRecipientEntity.class, uuid);
			}

			return dataRecipientEntity;
		}
	}
}
