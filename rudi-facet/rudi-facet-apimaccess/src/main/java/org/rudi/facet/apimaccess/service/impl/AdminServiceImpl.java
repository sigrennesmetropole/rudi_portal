package org.rudi.facet.apimaccess.service.impl;

import java.util.Arrays;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import org.rudi.facet.apimaccess.api.APIManagerProperties;
import org.rudi.facet.apimaccess.api.admin.AdminOperationAPI;
import org.rudi.facet.apimaccess.exception.AdminOperationException;
import org.rudi.facet.apimaccess.service.AdminService;
import org.springframework.stereotype.Service;
import org.wso2.carbon.apimgt.rest.api.admin.RoleAlias;
import org.wso2.carbon.apimgt.rest.api.admin.RoleAliasList;

import lombok.RequiredArgsConstructor;

@Service("apimAdminServiceImpl")
// renommé pour éviter les conflits avec org.rudi.microservice.kalim.service.admin.impl.AdminServiceImpl
@RequiredArgsConstructor
class AdminServiceImpl implements AdminService {
	private final AdminOperationAPI adminOperationAPI;

	private static boolean aliasHasRole(RoleAlias roleAlias, String role) {
		return roleAlias.getRole().trim().equals(role);
	}

	private static boolean aliasContainsRoleRudiUser(RoleAlias roleAlias) {
		return roleAlias.getAliases().stream().map(String::trim).anyMatch(alias -> alias.equals(APIManagerProperties.Roles.RUDI_USER));
	}

	@Override
	public void assignRoleToUser(String role, String username) throws AdminOperationException {
		final var systemScopesRoleAliases = adminOperationAPI.getSystemScopesRoleAliases();
		final var existingRoleAlias = getRoleAliasOfRole(role, systemScopesRoleAliases);
		if (existingRoleAlias == null) {
			final var newRoleAlias = new RoleAlias()
					.role(role)
					.aliases(Arrays.asList(
							role,
							APIManagerProperties.Roles.RUDI_USER
					));
			systemScopesRoleAliases.addListItem(newRoleAlias);
			systemScopesRoleAliases.count(systemScopesRoleAliases.getCount() + 1);
			adminOperationAPI.updateSystemScopesRoleAliases(systemScopesRoleAliases);
		}
	}

	@Nullable
	private RoleAlias getRoleAliasOfRole(String role, RoleAliasList aliasList) {
		final Predicate<RoleAlias> aliasHasRole = roleAlias -> aliasHasRole(roleAlias, role);
		final Predicate<RoleAlias> aliasContainsRoleRudiUser = AdminServiceImpl::aliasContainsRoleRudiUser;
		return aliasList.getList()
				.stream()
				.filter(aliasHasRole.and(aliasContainsRoleRudiUser))
				.findFirst().orElse(null);
	}
}
