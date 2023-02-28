package org.rudi.microservice.selfdata.service.utils;

import java.util.UUID;

import org.rudi.facet.acl.bean.User;
import org.rudi.facet.doks.policy.AuthorizationPolicy;
import org.springframework.stereotype.Component;

@Component
public class EverythingAllowedAuthorizationPolicy implements AuthorizationPolicy {
	public EverythingAllowedAuthorizationPolicy() {
		// Volontaire car ce policy fait un passe plat pour pouvoir appeler le service deleteAttachment()
	}

	@Override
	public boolean isAllowedToDownloadDocument(User authenticatedUser, UUID uploaderUuid) {
		return true;
	}

	@Override
	public boolean isAllowedToDeleteDocument(User authenticatedUser, UUID uploaderUuid) {
		return true; // Ce policy est appélé par le service internement, d'où l'autorisation permanente
	}
}
