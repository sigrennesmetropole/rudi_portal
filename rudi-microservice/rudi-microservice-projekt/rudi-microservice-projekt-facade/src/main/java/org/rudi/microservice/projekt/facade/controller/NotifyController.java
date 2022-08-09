package org.rudi.microservice.projekt.facade.controller;

import lombok.RequiredArgsConstructor;
import org.rudi.microservice.projekt.facade.controller.api.NotifyApi;
import org.rudi.microservice.projekt.service.notifications.NotifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotifyController implements NotifyApi {
	private final NotifyService notifyService;

	@Override
	public ResponseEntity<Void> handleAddOrganizationMember(UUID organizationUuid, UUID userUuid) throws Exception {
		notifyService.handleAddOrganizationMember(organizationUuid, userUuid);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Void> handleRemoveOrganizationMember(UUID organizationUuid, UUID userUuid) throws Exception {
		return null;
	}
}
