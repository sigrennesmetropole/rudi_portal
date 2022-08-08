package org.rudi.microservice.projekt.facade.controller;

import lombok.RequiredArgsConstructor;
import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;
import org.rudi.microservice.projekt.facade.controller.api.OwnerInfoApi;
import org.rudi.microservice.projekt.service.ownerinfo.OwnerInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OwnerInfoController implements OwnerInfoApi {
	private final OwnerInfoService ownerInfoService;

	@Override
	public ResponseEntity<OwnerInfo> getOwnerInfo(OwnerType ownerType, UUID ownerUuid) throws Exception {
		return ResponseEntity.ok(ownerInfoService.getOwnerInfo(ownerType, ownerUuid));
	}
}
