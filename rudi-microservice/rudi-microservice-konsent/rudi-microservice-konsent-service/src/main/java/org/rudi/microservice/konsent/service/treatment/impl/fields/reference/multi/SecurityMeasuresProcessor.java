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
import org.rudi.microservice.konsent.storage.dao.treatmentversion.SecurityMeasureDao;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.InvolvedPopulationCategoryEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.SecurityMeasureEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Component
@RequiredArgsConstructor
public class SecurityMeasuresProcessor implements CreateTreatmentVersionFieldProcessor, UpdateTreatmentVersionFieldProcessor {
	private final SecurityMeasureDao securityMeasureDao;
	private final SecurityMeasureEntityReplacer securityMeasureEntityReplacer = new SecurityMeasureEntityReplacer();

	@Override
	public void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException {
		if (treatmentVersion == null) {
			return;
		}
		securityMeasureEntityReplacer.replaceTransientEntitiesWithPersistentEntities(treatmentVersion, existingTreatmentVersion);
	}

	private class SecurityMeasureEntityReplacer extends TransientEntitiesReplacer<TreatmentVersionEntity, Set<SecurityMeasureEntity>> {

		public SecurityMeasureEntityReplacer() {
			super(TreatmentVersionEntity::getSecurityMeasures, TreatmentVersionEntity::setSecurityMeasures);
		}


		@Nullable
		@Override
		protected Set<SecurityMeasureEntity> getPersistentEntities(@Nullable Set<SecurityMeasureEntity> securityMeasureEntities) throws AppServiceException {
			if (securityMeasureEntities == null) {
				return null;
			}
			final Set<SecurityMeasureEntity> existingSecurityMeasures = new HashSet<>(securityMeasureEntities.size());
			for (final SecurityMeasureEntity securityMeasure : securityMeasureEntities) {
				final var existingSecurityMeasure = getExistingSecurityMeasure(securityMeasure);
				existingSecurityMeasures.add(existingSecurityMeasure);
			}
			return existingSecurityMeasures;
		}

		private SecurityMeasureEntity getExistingSecurityMeasure(SecurityMeasureEntity securityMeasure)
				throws MissingParameterException, AppServiceNotFoundException {
			val uuid = securityMeasure.getUuid();
			if (uuid == null) {
				throw new MissingParameterException("securityMeasure.uuid manquant");
			}

			SecurityMeasureEntity existingSecurityMesure;
			try {
				existingSecurityMesure = securityMeasureDao.findByUuid(uuid);
			} catch (EmptyResultDataAccessException e) {
				throw new AppServiceNotFoundException(securityMeasure, e);
			}

			if(existingSecurityMesure == null) {
				throw new AppServiceNotFoundException(SecurityMeasureEntity.class, uuid);
			}

			return existingSecurityMesure;
		}
	}
}
