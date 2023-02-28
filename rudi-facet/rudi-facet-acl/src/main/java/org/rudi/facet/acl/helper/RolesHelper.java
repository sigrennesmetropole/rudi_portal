package org.rudi.facet.acl.helper;

import org.apache.commons.collections4.CollectionUtils;
import org.rudi.common.core.security.Role;
import org.rudi.facet.acl.bean.User;
import org.springframework.stereotype.Component;

@Component
public class RolesHelper {

	public boolean hasAnyRole(final User user, final Role... roles) {
		final var userRoles = user.getRoles();
		if (CollectionUtils.isEmpty(userRoles)) {
			return false;
		}
		return userRoles.stream().anyMatch(userRole -> Role.containsRoleWithCode(roles, userRole.getCode()));
	}

}
