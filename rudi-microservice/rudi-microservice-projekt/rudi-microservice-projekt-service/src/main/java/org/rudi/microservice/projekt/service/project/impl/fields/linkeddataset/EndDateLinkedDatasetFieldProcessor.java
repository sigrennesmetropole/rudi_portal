package org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.rudi.common.service.exception.AppServiceBadRequestException;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.microservice.projekt.storage.entity.DatasetConfidentiality;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EndDateLinkedDatasetFieldProcessor implements CreateLinkedDatasetFieldProcessor, UpdateLinkedDatasetFieldProcessor {

	@Override
	public void process(@Nullable LinkedDatasetEntity linkedDataset, @Nullable LinkedDatasetEntity existingLinkedDataset) throws AppServiceException {
		// Si le JDD lié est ouvert, il ne doit pas avoir de date de fin
		if (linkedDataset != null && DatasetConfidentiality.OPENED == linkedDataset.getDatasetConfidentiality()) {
			linkedDataset.setEndDate(null);
		}

		// Si le JDD est restreint il DOIT avoir une date de fin
		if (linkedDataset != null && DatasetConfidentiality.RESTRICTED == linkedDataset.getDatasetConfidentiality()
				&& linkedDataset.getEndDate() == null) {
			throw new AppServiceBadRequestException("La date de fin d'accès est obligatoire pour une demande d'accès restreinte");
		}
	}
}
