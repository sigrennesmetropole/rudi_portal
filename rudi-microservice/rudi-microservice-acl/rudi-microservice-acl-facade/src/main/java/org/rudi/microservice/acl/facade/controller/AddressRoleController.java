package org.rudi.microservice.acl.facade.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.rudi.microservice.acl.core.bean.AddressRole;
import org.rudi.microservice.acl.core.bean.AddressRoleSearchCriteria;
import org.rudi.microservice.acl.core.bean.AddressType;
import org.rudi.microservice.acl.facade.controller.api.AddressRolesApi;
import org.rudi.microservice.acl.service.address.AddressRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controleur pour la gestion des roles d'adresses
 * 
 * @author MCY12700
 *
 */
@RestController
public class AddressRoleController implements AddressRolesApi {

	@Autowired
	private AddressRoleService addressRoleService;

	public AddressRoleController() {
		super();
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_ACL_ADMINISTRATOR')")
	public ResponseEntity<AddressRole> createAddressRole(@Valid AddressRole addressRole) throws Exception {
		return ResponseEntity.ok(addressRoleService.createAddressRole(addressRole));
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_ACL_ADMINISTRATOR')")
	public ResponseEntity<Void> deleteAddressRole(UUID uuid) throws Exception {
		addressRoleService.deleteAddressRole(uuid);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<AddressRole> getAddressRole(UUID uuid) throws Exception {
		return ResponseEntity.ok(addressRoleService.getAddressRole(uuid));
	}

	@Override
	public ResponseEntity<List<AddressRole>> searchAddressRoles(@Valid Boolean active, @Valid AddressType type)
			throws Exception {
		AddressRoleSearchCriteria searchCriteria = new AddressRoleSearchCriteria();
		searchCriteria.setActive(active);
		searchCriteria.setType(type);
		return ResponseEntity.ok(addressRoleService.searchAddressRoles(searchCriteria));
	}

	@Override
	@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'MODULE_ACL_ADMINISTRATOR')")
	public ResponseEntity<AddressRole> updateAddressRole(@Valid AddressRole addressRole) throws Exception {
		return ResponseEntity.ok(addressRoleService.updateAddressRole(addressRole));
	}
}
