package org.rudi.microservice.acl.facade.controller;

import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.microservice.acl.core.bean.CaptchaModel;
import org.rudi.microservice.acl.facade.controller.api.KaptchaApi;
import org.rudi.microservice.acl.service.captcha.service.CaptchaService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CaptchaController implements KaptchaApi {
	private final CaptchaService captchaService;
	private final ControllerHelper controllerHelper;

	@Override
	public ResponseEntity<Resource> generateCaptcha(String get, String c, String t, String cs, String d) throws Exception {
		return controllerHelper.downloadableResponseEntity(captchaService.generateCaptcha(get, c, t, cs, d));
	}

	@Override
	public ResponseEntity<Boolean> validateCaptcha(CaptchaModel captchaModel) throws Exception {
		return ResponseEntity.ok(captchaService.validateCaptcha(captchaModel));
	}
}
