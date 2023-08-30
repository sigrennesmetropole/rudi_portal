package org.rudi.microservice.acl.facade.controller;

import org.rudi.microservice.acl.core.bean.AppInfo;
import org.rudi.microservice.acl.facade.controller.api.ApplicationInformationApi;
import org.rudi.microservice.acl.service.config.ACLConfigurationServiceImpl;
import org.rudi.microservice.acl.service.mapper.AppInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;

@RestController
public class ApplicationInformationController implements ApplicationInformationApi {

	@Autowired
	private ACLConfigurationServiceImpl configurationService;

	@Autowired
	private AppInfoMapper appInfoMapper;

	@Override
	@ResponseBody
	public ResponseEntity<AppInfo> getApplicationInformation() {
		val appInfo = appInfoMapper.entityToDto(configurationService.getApplicationInformation());
		appInfo.setCaptchaEnabled(configurationService.isCaptchaEnabled());
		return ResponseEntity.ok(appInfo);
	}
}
