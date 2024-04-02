package org.rudi.microservice.acl.facade.controller;

import static org.rudi.common.core.security.QuotedRoleCodes.ADMINISTRATOR;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE;
import static org.rudi.common.core.security.QuotedRoleCodes.MODULE_ACL_ADMINISTRATOR;

import java.util.List;
import java.util.UUID;

import org.rudi.microservice.acl.core.bean.Role;
import org.rudi.microservice.acl.core.bean.RoleSearchCriteria;
import org.rudi.microservice.acl.facade.controller.api.RolesApi;
import org.rudi.microservice.acl.service.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controleur pour la gestion des roles des utilisateurs
 *
 * @author MCY12700
 */
@RestController
public class RoleController implements RolesApi {

	@Autowired
	private RoleService roleService;

	public RoleController() {
		super();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_ACL_ADMINISTRATOR + ")")
	public ResponseEntity<Role> createRole(Role role) throws Exception {
		return ResponseEntity.ok(roleService.createRole(role));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_ACL_ADMINISTRATOR + ")")
	public ResponseEntity<Void> deleteRole(UUID uuid) throws Exception {
		roleService.deleteRole(uuid);
		return ResponseEntity.ok().build();
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_ACL_ADMINISTRATOR + ", " + MODULE + ")")
	public ResponseEntity<Role> getRole(UUID uuid) throws Exception {
		return ResponseEntity.ok(roleService.getRole(uuid));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_ACL_ADMINISTRATOR + ", " + MODULE + ")")
	public ResponseEntity<List<Role>> searchRoles(String code, String label, Boolean active) throws Exception {
		RoleSearchCriteria searchCriteria = new RoleSearchCriteria();
		searchCriteria.setCode(code);
		searchCriteria.setLabel(label);
		searchCriteria.setActive(active);
		return ResponseEntity.ok(roleService.searchRoles(searchCriteria));
	}

	@Override
	@PreAuthorize("hasAnyRole(" + ADMINISTRATOR + ", " + MODULE_ACL_ADMINISTRATOR + ")")
	public ResponseEntity<Role> updateRole(Role role) throws Exception {
		return ResponseEntity.ok(roleService.updateRole(role));
	}
}
