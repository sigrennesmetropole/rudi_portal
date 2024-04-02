package org.rudi.microservice.strukture.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_STRUKTURE_ADMINISTRATOR;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.strukture.core.bean.AddressRole;
import org.rudi.microservice.strukture.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.strukture.core.bean.AddressType;
import org.rudi.microservice.strukture.facade.controller.api.AddressRolesApi;
import org.rudi.microservice.strukture.service.address.AddressRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AddressRoleController implements AddressRolesApi {

	@Autowired
	private AddressRoleService addressRoleService;

	public AddressRoleController() {
		super();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_STRUKTURE_ADMINISTRATOR + ")")
	public ResponseEntity<AddressRole> createAddressRole(AddressRole addressRole) throws Exception {
		return ResponseEntity.ok(addressRoleService.createAddressRole(addressRole));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_STRUKTURE_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteAddressRole(UUID uuid) throws Exception {
		addressRoleService.deleteAddressRole(uuid);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<AddressRole> getAddressRole(UUID uuid) throws Exception {
		return ResponseEntity.ok(addressRoleService.getAddressRole(uuid));
	}

	@Override
	public ResponseEntity<List<AddressRole>> searchAddressRoles(Boolean active, AddressType type) throws Exception {
		AddressRoleSearchCriteria searchCriteria = new AddressRoleSearchCriteria();
		searchCriteria.setActive(active);
		searchCriteria.setType(type);
		return ResponseEntity.ok(addressRoleService.searchAddressRoles(searchCriteria));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_STRUKTURE_ADMINISTRATOR + ")")
	public ResponseEntity<AddressRole> updateAddressRole(AddressRole addressRole) throws Exception {
		return ResponseEntity.ok(addressRoleService.updateAddressRole(addressRole));
	}
}
