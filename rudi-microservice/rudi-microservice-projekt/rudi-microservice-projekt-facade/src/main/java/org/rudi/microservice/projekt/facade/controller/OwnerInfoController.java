package org.rudi.microservice.projekt.facade.controller;

import java.util.UUID;

import org.rudi.microservice.projekt.core.bean.OwnerInfo;
import org.rudi.microservice.projekt.core.bean.OwnerType;
import org.rudi.microservice.projekt.facade.controller.api.OwnerInfoApi;
import org.rudi.microservice.projekt.service.ownerinfo.OwnerInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OwnerInfoController implements OwnerInfoApi {
	private final OwnerInfoService ownerInfoService;

	@Override
	public ResponseEntity<OwnerInfo> getOwnerInfo(OwnerType ownerType, UUID ownerUuid) throws Exception {
		return ResponseEntity.ok(ownerInfoService.getOwnerInfo(ownerType, ownerUuid));
	}

	@Override
	public ResponseEntity<Boolean> hasAccessToDataset(UUID uuidToCheck, UUID datasetUuid) throws Exception {
		return ResponseEntity.ok(ownerInfoService.hasAccessToDataset(uuidToCheck, datasetUuid));
	}

	@Override
	public ResponseEntity<UUID> getLinkedDatasetOwner(UUID linkedDatasetUuid) throws Exception {
		return ResponseEntity.ok(ownerInfoService.getLinkedDatasetOwner(linkedDatasetUuid));
	}

}
