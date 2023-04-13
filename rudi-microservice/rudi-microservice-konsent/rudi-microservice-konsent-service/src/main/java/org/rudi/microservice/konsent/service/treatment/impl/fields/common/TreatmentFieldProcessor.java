package org.rudi.microservice.konsent.service.treatment.impl.fields.common;

import javax.annotation.Nullable;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;

public interface TreatmentFieldProcessor {
	void process(@Nullable TreatmentEntity treatment, @Nullable TreatmentEntity existingTreatment) throws AppServiceException;
}
