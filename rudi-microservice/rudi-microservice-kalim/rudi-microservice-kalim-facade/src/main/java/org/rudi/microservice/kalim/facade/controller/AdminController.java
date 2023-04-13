package org.rudi.microservice.kalim.facade.controller;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.kalim.core.bean.OrganizationsReparationReport;
import org.rudi.microservice.kalim.facade.controller.api.AdminApi;
import org.rudi.microservice.kalim.service.admin.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;

@Controller
@RequiredArgsConstructor
public class AdminController implements AdminApi {

	private final AdminService adminService;

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> repairResources() throws Exception {
		adminService.repairResources();
		return ResponseEntity.noContent().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<OrganizationsReparationReport> repairOrganizations() throws Exception {
		return ResponseEntity.ok(adminService.repairOrganizations());
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> createMissingApis(List<UUID> globalIds) {
		adminService.createMissingApis(globalIds);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteAllApis() {
		adminService.deleteAllApis();
		return ResponseEntity.noContent().build();
	}
}
