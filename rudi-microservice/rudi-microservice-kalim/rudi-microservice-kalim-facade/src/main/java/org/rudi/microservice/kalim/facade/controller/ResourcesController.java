package org.rudi.microservice.kalim.facade.controller;

import org.rudi.common.service.exception.AppServiceException;
import org.rudi.common.service.exception.AppServiceUnauthorizedException;
import org.rudi.facet.dataverse.api.exceptions.DataverseAPIException;
import org.rudi.facet.kaccess.bean.Metadata;
import org.rudi.microservice.kalim.core.bean.IntegrationRequest;
import org.rudi.microservice.kalim.core.bean.Method;
import org.rudi.microservice.kalim.core.exception.IntegrationException;
import org.rudi.microservice.kalim.facade.controller.api.ResourcesApi;
import org.rudi.microservice.kalim.service.integration.IntegrationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ResourcesController implements ResourcesApi {

	@Autowired
	private IntegrationRequestService integrationRequestService;

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PROVIDER', 'MODULE', 'MODULE_KALIM_ADMINISTRATOR')")
	public ResponseEntity<UUID> createMetadata(Metadata metadata) {
		try {
			IntegrationRequest integrationRequest = integrationRequestService.createIntegrationRequest(metadata,
					Method.POST);
			return ResponseEntity.ok(integrationRequest.getUuid());
		} catch (AppServiceUnauthorizedException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (IntegrationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PROVIDER', 'MODULE', 'MODULE_KALIM_ADMINISTRATOR')")
	public ResponseEntity<UUID> updateMetadata(Metadata metadata) {
		try {
			IntegrationRequest integrationRequest = integrationRequestService.createIntegrationRequest(metadata,
					Method.PUT);

			return ResponseEntity.ok(integrationRequest.getUuid());
		} catch (AppServiceUnauthorizedException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		} catch (IntegrationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'PROVIDER', 'MODULE', 'MODULE_KALIM_ADMINISTRATOR')")
	public ResponseEntity<UUID> deleteMetadata(UUID globalId) throws IntegrationException, DataverseAPIException, AppServiceException {
		IntegrationRequest integrationRequest = integrationRequestService.createDeleteIntegrationRequestFromGlobalId(globalId);
		return ResponseEntity.ok(integrationRequest.getUuid());
	}

}
