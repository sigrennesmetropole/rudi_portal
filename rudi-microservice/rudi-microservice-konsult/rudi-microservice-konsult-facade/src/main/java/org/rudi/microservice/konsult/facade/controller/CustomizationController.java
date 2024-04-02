package org.rudi.microservice.konsult.facade.controller;

import org.rudi.common.core.DocumentContent;
import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.microservice.konsult.core.bean.CustomizationDescription;
import org.rudi.microservice.konsult.facade.controller.api.CustomizationsApi;
import org.rudi.microservice.konsult.service.customization.CustomizationService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CustomizationController implements CustomizationsApi {

	private final CustomizationService customizationService;
	private final ControllerHelper controllerHelper;


	@Override
	public ResponseEntity<CustomizationDescription> getCustomizationDescription(String lang) throws Exception {
		return ResponseEntity.ok(customizationService.getCustomizationDescription(lang));
	}

	@Override
	public ResponseEntity<Resource> downloadCustomizationResource(String resourceName) throws Exception {
		DocumentContent documentContent = customizationService.loadResources(resourceName);
		return controllerHelper.downloadableResponseEntity(documentContent);
	}


}
