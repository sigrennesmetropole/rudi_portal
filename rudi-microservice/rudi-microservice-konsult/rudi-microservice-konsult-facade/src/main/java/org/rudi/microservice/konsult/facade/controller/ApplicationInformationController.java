package org.rudi.microservice.konsult.facade.controller;

import org.rudi.common.service.configuration.ConfigurationService;
import org.rudi.microservice.konsult.core.bean.AppInfo;
import org.rudi.microservice.konsult.facade.controller.api.ApplicationInformationApi;
import org.rudi.microservice.konsult.service.mapper.AppInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
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
