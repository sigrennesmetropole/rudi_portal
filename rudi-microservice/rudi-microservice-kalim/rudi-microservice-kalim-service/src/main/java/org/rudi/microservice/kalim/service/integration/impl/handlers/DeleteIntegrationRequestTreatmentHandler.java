package org.rudi.microservice.kalim.service.integration.impl.handlers;

import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.helper.dataset.metadatadetails.MetadataDetailsHelper;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.DatasetCreatorIsAuthenticatedValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestErrorEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class DeleteIntegrationRequestTreatmentHandler extends IntegrationRequestTreatmentHandler {
	protected final DatasetCreatorIsAuthenticatedValidator datasetCreatorIsAuthenticatedValidator;
	protected final MetadataDetailsHelper metadataDetailsHelper;

	protected DeleteIntegrationRequestTreatmentHandler(DatasetService datasetService, APIManagerHelper apiManagerHelper, Error500Builder error500Builder, DatasetCreatorIsAuthenticatedValidator datasetCreatorIsAuthenticatedValidator, MetadataDetailsHelper metadataDetailsHelper) {
		super(datasetService, apiManagerHelper, error500Builder);
		this.datasetCreatorIsAuthenticatedValidator = datasetCreatorIsAuthenticatedValidator;
		this.metadataDetailsHelper = metadataDetailsHelper;
	}

	@Override
	protected void handleInternal(IntegrationRequestEntity integrationRequest) throws DataverseAPIException, APIManagerException {
		if (validateAndSetErrors(integrationRequest)) {
			treat(integrationRequest);
			integrationRequest.setIntegrationStatus(IntegrationStatus.OK);
		} else {
			integrationRequest.setIntegrationStatus(IntegrationStatus.KO);
		}
	}

	private boolean validateAndSetErrors(IntegrationRequestEntity integrationRequest) {
		final Set<IntegrationRequestErrorEntity> errors = new HashSet<>();

		if (datasetCreatorIsAuthenticatedValidator.canBeUsedBy(this)) {
			errors.addAll(datasetCreatorIsAuthenticatedValidator.validate(integrationRequest));
		}

		// Sauvegarde des erreurs
		integrationRequest.setErrors(errors);

		return errors.isEmpty();
	}

	void treat(IntegrationRequestEntity integrationRequest) throws DataverseAPIException, APIManagerException {
		final UUID globalId = integrationRequest.getGlobalId();

		var restricted = false;
		try {
			final var metadataToDelete = datasetService.getDataset(globalId);
			restricted = metadataDetailsHelper.isRestricted(metadataToDelete);
			datasetService.archiveDataset(metadataToDelete.getDataverseDoi());
		} catch (final DatasetNotFoundException e) {
			log.info("Dataset {} to delete was not found", globalId);
		}

		apiManagerHelper.archiveAllAPI(integrationRequest, restricted);
	}
}
