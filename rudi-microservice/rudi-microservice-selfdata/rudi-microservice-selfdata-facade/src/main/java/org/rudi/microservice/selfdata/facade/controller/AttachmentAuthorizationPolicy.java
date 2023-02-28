package org.rudi.microservice.selfdata.facade.controller;

import java.util.UUID;

import org.rudi.facet.acl.bean.User;
import org.rudi.facet.acl.helper.RolesHelper;
import org.rudi.facet.doks.policy.AuthorizationPolicy;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import static org.rudi.common.core.security.Role.ADMINISTRATOR;
import static org.rudi.common.core.security.Role.MODERATOR;

@Component
@RequiredArgsConstructor
class AttachmentAuthorizationPolicy implements AuthorizationPolicy {

	private final RolesHelper rolesHelper;

	@Override
	public boolean isAllowedToDownloadDocument(User authenticatedUser, UUID uploaderUuid) {
		return isAllowedToAccess(authenticatedUser, uploaderUuid);
	}

	private boolean isAllowedToAccess(User authenticatedUser, UUID uploaderUuid) {
		if (rolesHelper.hasAnyRole(authenticatedUser, ADMINISTRATOR, MODERATOR)) {
			return true;
		}
		return authenticatedUser.getUuid().equals(uploaderUuid);
	}

	@Override
	public boolean isAllowedToDeleteDocument(User authenticatedUser, UUID uploaderUuid) {
		return isAllowedToAccess(authenticatedUser, uploaderUuid);
	}
}
