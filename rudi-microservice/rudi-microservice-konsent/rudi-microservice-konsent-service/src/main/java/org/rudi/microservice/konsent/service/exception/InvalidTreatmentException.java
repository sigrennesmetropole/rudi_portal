package org.rudi.microservice.konsent.service.exception;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;

public class InvalidTreatmentException extends AppServiceException {

	public InvalidTreatmentException(String message) {
		super(message);
	}

	public InvalidTreatmentException(TreatmentEntity treatmentEntity, String message) {
		this("Le traitement d'UUID " + treatmentEntity.getUuid()
				+ " n'est pas dans un état valide pour le traitement appliqué : " + message);
	}
}
