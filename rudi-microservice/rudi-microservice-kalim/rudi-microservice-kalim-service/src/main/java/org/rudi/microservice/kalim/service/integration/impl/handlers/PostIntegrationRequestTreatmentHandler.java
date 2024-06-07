package org.rudi.microservice.kalim.service.integration.impl.handlers;

import java.util.List;

import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.facet.organization.helper.OrganizationHelper;
import org.rudi.microservice.kalim.service.helper.ApiManagerHelper;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.service.integration.impl.validator.AbstractMetadataValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.DatasetCreatorIsAuthenticatedValidator;
import org.rudi.microservice.kalim.service.integration.impl.validator.MetadataInfoProviderIsAuthenticatedValidator;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Primary
@Slf4j
public class PostIntegrationRequestTreatmentHandler extends AbstractIntegrationRequestTreatmentHandlerWithValidation {

	protected PostIntegrationRequestTreatmentHandler(DatasetService datasetService,
			ApiManagerHelper apigatewayManagerHelper, APIManagerHelper apiManagerHelper, ObjectMapper objectMapper,
			List<AbstractMetadataValidator<?>> metadataValidators, Error500Builder error500Builder,
			MetadataInfoProviderIsAuthenticatedValidator metadataInfoProviderIsAuthenticatedValidator,
			DatasetCreatorIsAuthenticatedValidator datasetCreatorIsAuthenticatedValidator,
			OrganizationHelper organizationHelper) {
		super(datasetService, apigatewayManagerHelper, apiManagerHelper, objectMapper, metadataValidators,
				error500Builder, metadataInfoProviderIsAuthenticatedValidator, datasetCreatorIsAuthenticatedValidator,
				organizationHelper);
	}

	@Override
	protected void treat(IntegrationRequestEntity integrationRequest, Metadata metadata)
			throws DataverseAPIException, APIManagerException {
		final String doi = datasetService.createDataset(metadata);
		try {
			final Metadata metadataCreated = datasetService.getDataset(doi);
			createApi(integrationRequest, metadataCreated);
		} catch (RuntimeException e) {
			log.error("On va supprimer le JDD qui vient d'être créé car une erreur est survenue", e);
			datasetService.deleteDataset(doi);
			throw e;
		}
	}

	private void createApi(IntegrationRequestEntity integrationRequest, Metadata metadataCreated)
			throws DataverseAPIException, APIManagerException {
		try {
			apiManagerHelper.createAPI(integrationRequest, metadataCreated);
			apiGatewayManagerHelper.createApis(integrationRequest, metadataCreated);
		} catch (final APIManagerException | RuntimeException e) {
			datasetService.deleteDataset(metadataCreated.getGlobalId());
			throw e;
		}
	}

}
