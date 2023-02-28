package org.rudi.facet.doks.policy;

import java.util.UUID;

import org.rudi.facet.acl.bean.User;

public interface AuthorizationPolicy {
	boolean isAllowedToDownloadDocument(User authenticatedUser, UUID uploaderUuid);

	boolean isAllowedToDeleteDocument(User authenticatedUser, UUID uploaderUuid);
}
