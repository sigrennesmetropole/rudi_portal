package org.rudi.microservice.konsult.facade.controller.api;

import lombok.RequiredArgsConstructor;

import org.rudi.facet.apimaccess.bean.Credentials;
import org.rudi.microservice.konsult.core.bean.ApiKeys;
import org.rudi.microservice.konsult.core.bean.ApiKeysType;
import org.rudi.microservice.konsult.service.apim.ApimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApimController implements ApimApi {

	private final ApimService apimService;

	@Override
	public ResponseEntity<Boolean> hasEnabledApi(Credentials credentials) throws Exception {
		return ResponseEntity.ok(apimService.hasEnabledApi(credentials));
	}

	@Override
	public ResponseEntity<Void> enableApi(Credentials credentials) throws Exception {
		apimService.enableApi(credentials);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<ApiKeys> getKeys(ApiKeysType type, Credentials credentials) throws Exception {
		return ResponseEntity.ok(apimService.getKeys(type, credentials));
	}
}
