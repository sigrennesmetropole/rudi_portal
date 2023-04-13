package org.rudi.microservice.konsent.service.treatment.impl.fields.common;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;

interface TreatmentVersionFieldProcessor {
	void process(@Nullable TreatmentVersionEntity treatmentVersion, @Nullable TreatmentVersionEntity existingTreatmentVersion) throws AppServiceException;
}
