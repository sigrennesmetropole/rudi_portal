package org.rudi.microservice.konsent.service.helper;


import java.security.InvalidParameterException;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.microservice.konsent.service.exception.InvalidTreatmentException;
import org.rudi.microservice.konsent.storage.entity.common.TreatmentStatus;
import org.rudi.microservice.konsent.storage.entity.treatment.TreatmentEntity;
import org.rudi.microservice.konsent.storage.entity.treatmentversion.TreatmentVersionEntity;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TreatmentHelper {

	public boolean hasNoDraft(TreatmentEntity treatmentEntity) {

		// Aucune version, pas de DRAFT
		if (treatmentEntity.getVersions() == null) {
			return true;
		}

		// Renvoie faux si au moins 1 version est à DRAFT
		return treatmentEntity.getVersions().stream()
				.noneMatch(treatmentVersion -> treatmentVersion.getStatus().equals(TreatmentStatus.DRAFT));
	}

	@NonNull
	public TreatmentVersionEntity findDraftVersion(TreatmentEntity treatmentEntity) throws InvalidTreatmentException {

		if (treatmentEntity == null) {
			throw new InvalidTreatmentException("Impossible de chercher une version pour un traitement nul");
		}

		if (CollectionUtils.isEmpty(treatmentEntity.getVersions())) {
			throw new InvalidTreatmentException(treatmentEntity,
					"Impossible de chercher une version pour un traitement sans versions");
		}

		return treatmentEntity.getVersions().stream()
				.filter(treatmentVersion -> treatmentVersion.getStatus().equals(TreatmentStatus.DRAFT))
				.findFirst()
				.orElseThrow(
						() -> new InvalidTreatmentException(
								treatmentEntity, "le traitement fourni n'a pas de version draft"
						)
				);
	}
}
