package org.rudi.microservice.kalim.service.integration.impl.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.DatasetCreatorIsAuthenticatedValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.MetadataInfoProviderIsAuthenticatedValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PutIntegrationRequestTreatmentHandler extends AbstractIntegrationRequestTreatmentHandlerWithValidation {

	protected PutIntegrationRequestTreatmentHandler(DatasetService datasetService, APIManagerHelper apiManagerHelper, ObjectMapper objectMapper, List<AbstractMetadataValidator<?>> metadataValidators, Error500Builder error500Builder, MetadataInfoProviderIsAuthenticatedValidator metadataInfoProviderIsAuthenticatedValidator, DatasetCreatorIsAuthenticatedValidator datasetCreatorIsAuthenticatedValidator, OrganizationHelper organizationHelper) {
		super(datasetService, apiManagerHelper, objectMapper, metadataValidators, error500Builder, metadataInfoProviderIsAuthenticatedValidator, datasetCreatorIsAuthenticatedValidator, organizationHelper);
	}

	@Override
	protected void treat(IntegrationRequestEntity integrationRequest, Metadata metadata) throws DataverseAPIException, APIManagerException {
		// récupération des métadonnées à partir du globalid, pour récupérer le dataverse doi
		final Metadata actualMetadata = datasetService.getDataset(metadata.getGlobalId());
		metadata.setDataverseDoi(actualMetadata.getDataverseDoi());

		updateApi(integrationRequest, metadata, actualMetadata);
	}

	private void updateApi(IntegrationRequestEntity integrationRequest, Metadata metadata, Metadata actualMetadata) throws DataverseAPIException, APIManagerException {
		try {
			// Mise à jour du jeu de données dans le dataverse
			final Metadata metadataUpdated = datasetService.updateDataset(metadata);
			// mise à jour de l'API dans l'APi manager
			apiManagerHelper.updateAPI(integrationRequest, metadataUpdated, actualMetadata);
		} catch (final DataverseAPIException | APIManagerException | RuntimeException e) {
			// restauration du jdd dans le dataverse en cas d'erreur lors de la mise à jour WSO2
			datasetService.updateDataset(actualMetadata);
			throw e;
		}
	}
}
