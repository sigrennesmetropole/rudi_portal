package org.rudi.microservice.kalim.service.integration.impl.handlers;

import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DatasetNotFoundException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DeleteIntegrationRequestTreatmentHandler extends IntegrationRequestTreatmentHandler {

	DeleteIntegrationRequestTreatmentHandler(DatasetService datasetService, APIManagerHelper apiManagerHelper, Error500Builder error500Builder) {
		super(datasetService, apiManagerHelper, error500Builder);
	}

	@Override
	protected void handleInternal(IntegrationRequestEntity integrationRequest) throws DataverseAPIException, APIManagerException {
		treat(integrationRequest);
		integrationRequest.setIntegrationStatus(IntegrationStatus.OK);
	}

	void treat(IntegrationRequestEntity integrationRequest) throws DataverseAPIException, APIManagerException {
		final UUID globalId = integrationRequest.getGlobalId();
		try {
			final Metadata metadataToDelete = datasetService.getDataset(globalId);
			datasetService.archiveDataset(metadataToDelete.getDataverseDoi());
		} catch (DatasetNotFoundException e) {
			log.warn("Cannot archive non existing dataset " + globalId);
		}
		apiManagerHelper.deleteAPI(integrationRequest, globalId);
	}
}
