package org.rudi.microservice.apigateway.facade.controller;

import org.rudi.common.service.configuration.ConfigurationService;
import org.rudi.microservice.apigateway.core.bean.AppInfo;
import org.rudi.microservice.apigateway.facade.controller.api.ApplicationInformationApi;
import org.rudi.microservice.apigateway.service.mapper.AppInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationInformationController implements ApplicationInformationApi {

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private AppInfoMapper appInfoMapper;

	@Override
	@ResponseBody
	public ResponseEntity<AppInfo> getApplicationInformation() {
		return ResponseEntity.ok(appInfoMapper.entityToDto(configurationService.getApplicationInformation()));
	}
}
