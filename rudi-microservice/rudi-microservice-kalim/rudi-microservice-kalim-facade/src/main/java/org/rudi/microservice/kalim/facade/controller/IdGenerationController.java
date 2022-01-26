/**
 * RUDI Portail
 */
package org.rudi.microservice.kalim.facade.controller;

import java.util.UUID;

import org.rudi.microservice.kalim.facade.controller.api.IdGenerationApi;
import org.rudi.microservice.kalim.service.integration.IntegrationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author FNI18300
 *
 */
@RestController
public class IdGenerationController implements IdGenerationApi {

	@Autowired
	private IntegrationRequestService integrationRequestService;

	@Override
	public ResponseEntity<UUID> generateId() {
		return ResponseEntity.ok(integrationRequestService.generateMetaDataId());
	}

}
