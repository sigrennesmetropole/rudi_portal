package org.rudi.microservice.projekt.facade.controller;

import lombok.RequiredArgsConstructor;
import org.rudi.common.service.configuration.ConfigurationService;
import org.rudi.microservice.projekt.core.bean.AppInfo;
import org.rudi.microservice.projekt.facade.controller.api.ApplicationInformationApi;
import org.rudi.microservice.projekt.service.mapper.AppInfoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ApplicationInformationController implements ApplicationInformationApi {

	private final ConfigurationService configurationService;
	private final AppInfoMapper appInfoMapper;

	@Override
	@ResponseBody
	public ResponseEntity<AppInfo> getApplicationInformation() {
		return ResponseEntity.ok(appInfoMapper.entityToDto(configurationService.getApplicationInformation()));
	}
}
