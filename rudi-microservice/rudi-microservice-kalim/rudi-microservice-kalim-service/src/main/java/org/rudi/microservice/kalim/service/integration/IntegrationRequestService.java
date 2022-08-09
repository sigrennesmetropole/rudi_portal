package org.rudi.microservice.kalim.service.integration;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.facet.providers.bean.NodeProvider;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.IntegrationRequestSearchCriteria;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service de gestion des demandes d'intégration
 *
 */
public interface IntegrationRequestService {

	Page<IntegrationRequest> searchIntegrationRequests(IntegrationRequestSearchCriteria searchCriteria,
			Pageable pageable);

	IntegrationRequest createIntegrationRequest(Metadata metadata, Method method)
			throws AppServiceUnauthorizedException, IntegrationException;

	IntegrationRequest createDeleteIntegrationRequestFromGlobalId(UUID globalId) throws DataverseAPIException, IntegrationException, AppServiceException;

	IntegrationRequest createIntegrationRequestFromHarvesting(Metadata metadata, Method method, NodeProvider nodeProvider)
			throws IllegalAccessException, IntegrationException;

	void handleIntegrationRequest(UUID uuid) throws IntegrationException;

	UUID generateMetaDataId();
}
