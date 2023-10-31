package org.rudi.microservice.projekt.service.project.impl.fields.linkeddataset;

import org.jetbrains.annotations.Nullable;
import org.rudi.common.service.exception.AppServiceException;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.microservice.projekt.service.helper.linkeddataset.LinkedDatasetSubscriptionHelper;
import org.rudi.microservice.projekt.storage.entity.linkeddataset.LinkedDatasetEntity;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Order(2)
@RequiredArgsConstructor
public class LinkedDatasetSubscriptionProcessor implements DeleteLinkedDatasetFieldProcessor {

	private final LinkedDatasetSubscriptionHelper linkedDatasetSubscriptionHelper;

	@Override
	public void process(@Nullable LinkedDatasetEntity linkedDataset,
			@Nullable LinkedDatasetEntity existingLinkedDataset) throws AppServiceException, APIManagerException {
		// Tentative de suppression de la souscription accordée par ce linked dataset
		linkedDatasetSubscriptionHelper.handleUnlinkLinkedDataset(existingLinkedDataset);
	}
}
