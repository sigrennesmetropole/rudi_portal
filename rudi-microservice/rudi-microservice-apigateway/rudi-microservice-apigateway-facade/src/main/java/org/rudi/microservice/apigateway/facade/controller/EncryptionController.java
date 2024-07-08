package org.rudi.microservice.apigateway.facade.controller;

import java.util.UUID;

import org.rudi.common.facade.helper.ControllerHelper;
import org.rudi.microservice.apigateway.facade.controller.api.EncryptionKeyApi;
import org.rudi.microservice.apigateway.service.encryption.EncryptionService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class EncryptionController implements EncryptionKeyApi {

	private final EncryptionService encryptionService;

	private final ControllerHelper controllerHelper;

	@Override
	public ResponseEntity<Resource> getEncryptionKey(UUID mediaId) throws Exception {
		return controllerHelper
				.downloadableResponseEntity(encryptionService.getPublicEncryptionKeyAsDocumentContent(mediaId));
	}
}
