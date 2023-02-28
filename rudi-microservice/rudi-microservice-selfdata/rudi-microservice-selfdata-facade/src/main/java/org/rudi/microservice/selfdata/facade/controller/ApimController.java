package org.rudi.microservice.selfdata.facade.controller;

import org.rudi.facet.apimaccess.bean.Credentials;
import org.rudi.microservice.selfdata.facade.controller.api.ApimApi;
import org.rudi.microservice.selfdata.service.apim.ApimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ApimController implements ApimApi {

	private final ApimService apimService;

	@Override
	public ResponseEntity<Void> enableApi(Credentials credentials) throws Exception {
		apimService.enableApi(credentials);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Boolean> hasEnabledApi(Credentials credentials) throws Exception {
		return ResponseEntity.ok(apimService.hasEnabledApi(credentials));
	}
}
