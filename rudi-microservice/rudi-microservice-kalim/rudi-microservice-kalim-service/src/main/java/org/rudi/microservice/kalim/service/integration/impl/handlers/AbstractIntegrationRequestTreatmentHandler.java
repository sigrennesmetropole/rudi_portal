package org.rudi.microservice.kalim.service.integration.impl.handlers;

import java.util.Collections;

import org.rudi.facet.apimaccess.exception.APIManagerException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.service.dataset.DatasetService;
import org.rudi.microservice.kalim.core.bean.IntegrationStatus;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.service.helper.ApiManagerHelper;
import org.rudi.microservice.kalim.service.helper.Error500Builder;
import org.rudi.microservice.kalim.service.helper.apim.APIManagerHelper;
import org.rudi.microservice.kalim.storage.entity.integration.IntegrationRequestEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractIntegrationRequestTreatmentHandler {

	protected final DatasetService datasetService;
	protected final ApiManagerHelper apiGatewayManagerHelper;
	protected final APIManagerHelper apiManagerHelper;
	private final Error500Builder error500Builder;

	public void handle(IntegrationRequestEntity integrationRequest) {
		try {
			handleInternal(integrationRequest);
		} catch (Exception e) {
			integrationRequest.setIntegrationStatus(IntegrationStatus.KO);
			integrationRequest.setErrors(Collections.singleton(error500Builder.build()));
			log.info("Handle Integration request treatment KO.", e);
		}
	}

	protected abstract void handleInternal(IntegrationRequestEntity integrationRequest)
			throws IntegrationException, DataverseAPIException, APIManagerException;

}
