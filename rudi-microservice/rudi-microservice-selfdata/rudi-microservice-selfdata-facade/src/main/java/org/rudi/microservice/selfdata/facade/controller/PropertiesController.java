package org.rudi.microservice.selfdata.facade.controller;

import java.util.List;

import org.rudi.microservice.selfdata.core.bean.FrontOfficeProperties;
import org.rudi.microservice.selfdata.core.bean.SelfdataRequestAllowedAttachementType;
import org.rudi.microservice.selfdata.facade.controller.api.PropertiesApi;
import org.rudi.microservice.selfdata.service.properties.PropertiesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PropertiesController implements PropertiesApi {

	private final PropertiesService propertiesService;

	@Override
	public ResponseEntity<FrontOfficeProperties> getFrontOfficeProperties() throws Exception {
		return ResponseEntity.ok(propertiesService.getFrontOfficeProperties());
	}

	@Override
	public ResponseEntity<List<SelfdataRequestAllowedAttachementType>> getAllowedAttachementTypes() throws Exception {
		return ResponseEntity.ok(propertiesService.getAllowedAttachementTypes());
	}
}
